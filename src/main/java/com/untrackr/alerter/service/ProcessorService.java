package com.untrackr.alerter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.filter.Print;
import com.untrackr.alerter.processor.producer.Console;
import com.untrackr.alerter.processor.special.Pipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.untrackr.alerter.common.ApplicationUtil.environmentVariable;
import static com.untrackr.alerter.common.ApplicationUtil.property;

@Service
public class ProcessorService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private FactoryService factoryService;

	@Autowired
	private FileTailingService fileTailingService;

	private List<File> descriptorDirectories;

	private String hostName;

	private ObjectMapper objectMapper;

	private ThreadPoolExecutor runnerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
			60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
			ThreadUtil.threadFactory("Runner"));

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("ScheduledTask"));
	;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Path
		String path = property("ALERTER_DESCRIPTOR_PATH");
		String[] directoryStrings = path.split(":");
		descriptorDirectories = new ArrayList<>();
		for (String directoryString : directoryStrings) {
			descriptorDirectories.add(new File(directoryString));
		}
		// Object mapper
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		// Hostname
		String hostNameArg = environmentVariable("ALERTER_HOSTNAME", null);
		if (hostNameArg != null) {
			hostName = hostNameArg;
		} else {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				throw new IllegalStateException("cannot determine hostname; please define environment variable ALERTER_HOSTNAME");
			}
		}
		logger.info("Hostname: " + hostName);
	}


	public void start() {
		String[] filenames = property("ALERTER_RUNNERS").split(",");
		List<Processor> toplevelProcessors = new ArrayList<>();
		withErrorHandline(null, null, () -> {
			for (String filename : filenames) {
				IncludePath emptyPath = new IncludePath();
				Processor processor = factoryService.loadProcessor(filename);
				List<Processor> processors = new ArrayList<>();
				if (processor.getSignature().getInputRequirement() == ProcessorSignature.PipeRequirement.required) {
					processors.add(new Console(this, emptyPath));
				}
				processors.add(processor);
				if (processor.getSignature().getOutputRequirement() == ProcessorSignature.PipeRequirement.required) {
					processors.add(new Print(this, emptyPath));
				}
				Pipe pipe = new Pipe(this, processors, emptyPath);
				pipe.check();
				toplevelProcessors.add(pipe);
				initializeConcurrently(toplevelProcessors);
			}
		});
	}

	public void displayValidationError(ValidationError e) {
		StringWriter builder = new StringWriter();
		IncludePath path = e.getPath();
		if ((path != null) && !path.isEmpty()) {
			builder.append(path.path()).append(": ");
		}
		builder.append("error: ").append(e.getLocalizedMessage());
		if ((e.getCause() != null) && !(e.getCause() instanceof IOException)) {
			builder.append("\nStack trace:\n");
			e.printStackTrace(new PrintWriter(builder));
		}
		logger.error(builder.toString());
	}

	public void processorAlert(Alert.Priority priority, String title, Payload payload, Processor consumer) {
		StringBuilder builder = new StringBuilder();
		builder.append("Input: ").append(payload.asText()).append("\n");
		builder.append("Processor: ").append(payload.path(consumer)).append("\n");
		Alert alert = new Alert(priority, title, builder.toString());
		alertService.alert(alert);
	}

	public void displayRuntimeError(RuntimeProcessorError e) {
		StringWriter builder = new StringWriter();
		builder.append("Error: ").append(e.getLocalizedMessage()).append("\n");
		Payload payload = e.getPayload();
		if (payload != null) {
			builder.append("Input: ").append(payload.asText()).append("\n");
		}
		Processor processor = e.getProcessor();
		builder.append("Processor: ").append(payload.path(processor)).append("\n");
		if ((e.getCause() != null) && !(e.getCause() instanceof IOException)) {
			builder.append("\nStack trace:\n");
			e.printStackTrace(new PrintWriter(builder));
		}
		String message = builder.toString();
		logger.error(message);
		Alert alert = new Alert(Alert.Priority.emergency, "Alerter error: " + e.getLocalizedMessage(), message);
		alertService.alert(alert);
	}

	public void infrastructureAlert(Alert.Priority priority, String title, String details, Throwable t) {
		String prefixedTitle = "Alerter error: " + title;
		String message = details + "\n" + t.toString();
		StackTraceElement[] stack = t.getStackTrace();
		if (stack.length != 0) {
			message = message + "\n" + stack[0].toString();
		}
		logger.error(title + ": " + details, t);
		alertService.alert(new Alert(priority, prefixedTitle, message));
	}

	public void consumeConcurrently(List<Processor> consumers, Payload payload) {
		for (Processor consumer : consumers) {
			runnerExecutor.execute(() -> {
				withErrorHandline(consumer, payload, () -> consumer.consume(payload));
			});
		}
	}

	public void initializeConcurrently(List<Processor> toplevelProcessors) {
		// All processors are independant and could be initialized concurrently; that would require waiting for all the
		// initialization to be terminated. For simplicity's sake, we initialize them in sequence.
		toplevelProcessors.forEach(processor -> withErrorHandline(processor, null, processor::initialize));
	}

	public void withErrorHandline(Processor processor, Payload payload, Runnable runnable) {
		try {
			runnable.run();
		} catch (RuntimeProcessorError e) {
			displayRuntimeError(e);
		} catch (ValidationError e) {
			displayValidationError(e);
		} catch (Throwable t) {
			displayRuntimeError(new RuntimeProcessorError(t, processor, payload));
		}
	}

	public File findInPath(String filename) {
		for (File directory : descriptorDirectories) {
			File file = new File(directory, filename);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		ThreadUtil.safeExecutorShutdownNow(runnerExecutor, "RunnerExecutor", profileService.profile().getExecutorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "ScheduledExecutor", profileService.profile().getExecutorTerminationTimeout());
	}

	public AlertService getAlertService() {
		return alertService;
	}

	public ProfileService getProfileService() {
		return profileService;
	}

	public FactoryService getFactoryService() {
		return factoryService;
	}

	public FileTailingService getFileTailingService() {
		return fileTailingService;
	}

	public ScheduledThreadPoolExecutor getScheduledExecutor() {
		return scheduledExecutor;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public String getHostName() {
		return hostName;
	}

}
