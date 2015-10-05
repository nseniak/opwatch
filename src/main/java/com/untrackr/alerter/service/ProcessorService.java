package com.untrackr.alerter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.untrackr.alerter.common.InternalScriptError;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.filter.print.Print;
import com.untrackr.alerter.processor.producer.console.Console;
import com.untrackr.alerter.processor.special.pipe.Pipe;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
import java.util.regex.PatternSyntaxException;

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

	@Autowired
	private ConsoleService consoleService;

	@Autowired
	private HttpService httpService;

	@Autowired
	private ApplicationContext applicationContext;

	private List<File> descriptorDirectories;

	private String hostName;

	private ObjectMapper objectMapper;

	private NashornScriptEngine nashorn;

	private Processor mainProcessor;

	private static final String DELIMITER = "\n--\n";
	private static final int MAX_INPUT_LENGTH = 120;

	private ThreadPoolExecutor consumerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
			60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
			ThreadUtil.threadFactory("Consumer"));

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("ScheduledTask"));

	@Override
	public void afterPropertiesSet() throws Exception {
		// Path
		descriptorDirectories = new ArrayList<>();
		String path = property("ALERTER_DESCRIPTOR_PATH", null);
		if (path != null) {
			String[] directoryStrings = path.split(":");
			for (String directoryString : directoryStrings) {
				descriptorDirectories.add(new File(directoryString));
			}
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
		// Nashorn
		nashorn = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		logger.info("Hostname: " + hostName);
	}


	public void start() {
		String filename = property("ALERTER_MAIN");
		boolean error = withErrorHandling(null, null, () -> {
			IncludePath emptyPath = new IncludePath();
			mainProcessor = factoryService.loadProcessor(filename, emptyPath);
			if (profileService.profile().isInteractive()) {
				List<Processor> pipeProcessors = new ArrayList<>();
				if (mainProcessor.getSignature().getInputRequirement() == ProcessorSignature.PipeRequirement.required) {
					logger.info("Adding \"console\" as input processor");
					pipeProcessors.add(new Console(this, emptyPath));
				}
				pipeProcessors.add(mainProcessor);
				if (mainProcessor.getSignature().getOutputRequirement() == ProcessorSignature.PipeRequirement.required) {
					logger.info("Adding \"print\" as output processor");
					pipeProcessors.add(new Print(this, emptyPath));
				}
				if (pipeProcessors.size() != 1) {
					mainProcessor = new Pipe(this, pipeProcessors, emptyPath);
				}
			}
			mainProcessor.check();
			mainProcessor.start();
			if (!mainProcessor.started()) {
				throw new RuntimeProcessorError("cannot start main processor", mainProcessor, null);
			}
		});
		if (error) {
			logger.info("Exiting due to startup errors");
			SpringApplication.exit(applicationContext);
		} else {
			Alert alert = new Alert(Alert.Priority.low, "Alerter running on " + getHostName(), "--");
			alertService.alert(alert);
		}
	}

	public void displayValidationError(ValidationError e) {
		StringWriter builder = new StringWriter();
		IncludePath path = e.getPath();
		if ((path != null) && !path.isEmpty()) {
			builder.append(path.pathDescriptor()).append(": ");
		}
		builder.append("error: ").append(e.getLocalizedMessage());
		logger.error(builder.toString());
		builder.append(DELIMITER);
		builder.append("Hostname:\n").append(getHostName()).append(DELIMITER);
		Throwable c = e.getCause();
		if ((c != null) && !((c instanceof IOException) || (c instanceof PatternSyntaxException))) {
			builder.append("Stack trace:\n");
			e.printStackTrace(new PrintWriter(builder));
			builder.append(DELIMITER);
		}
		String message = builder.toString();
		Alert alert = new Alert(Alert.Priority.emergency, "Alerter startup error: " + e.getLocalizedMessage(), message);
		alertService.alert(alert);
	}

	public void processorAlert(Alert.Priority priority, String title, Payload payload, Processor consumer) {
		StringBuilder builder = new StringBuilder();
		builder.append("Hostname:\n").append(getHostName()).append(DELIMITER);
		builder.append("Alert path:\n").append(payload.pathDescriptor(consumer)).append(DELIMITER);
		builder.append("Input:\n").append(truncate(payload.asText(), MAX_INPUT_LENGTH)).append(DELIMITER);
		Alert alert = new Alert(priority, title, builder.toString());
		alertService.alert(alert);
	}

	public void displayRuntimeError(RuntimeProcessorError e) {
		StringWriter builder = new StringWriter();
		builder.append("Hostname:\n").append(getHostName()).append(DELIMITER);
		builder.append("Error:\n").append(e.getLocalizedMessage()).append(DELIMITER);
		Processor processor = e.getProcessor();
		Payload payload = e.getPayload();
		if (payload != null) {
			builder.append("Processor:\n").append(payload.pathDescriptor(processor)).append(DELIMITER);
			builder.append("Input:\n").append(truncate(payload.asText(), MAX_INPUT_LENGTH)).append(DELIMITER);
		} else {
			builder.append("Processor:\n").append(processor.pathDescriptor()).append(DELIMITER);
		}
		if ((e.getCause() != null) && !((e.getCause() instanceof IOException) || (e.getCause() instanceof ScriptException) || (e.getCause() instanceof InternalScriptError))) {
			logger.error(builder.toString(), e);
			builder.append("Stack trace:\n");
			e.printStackTrace(new PrintWriter(builder));
			builder.append(DELIMITER);
		} else {
			logger.error(builder.toString());
		}
		String message = builder.toString();
		Alert alert = new Alert(Alert.Priority.emergency, "Error: " + e.getLocalizedMessage(), message);
		alertService.alert(alert);
	}

	private String truncate(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, Math.max(0, length - 3)) + "...";
		}
	}

	public void infrastructureAlert(Alert.Priority priority, String title, String details) {
		infrastructureAlert(priority, title, details, null);
	}

	public void infrastructureAlert(Alert.Priority priority, String title, String details, Throwable t) {
		String prefixedTitle = "Infrastructure error: " + title;
		StringWriter builder = new StringWriter();
		builder.append("Hostname:\n").append(getHostName()).append(DELIMITER);
		builder.append("Details:\n").append(details).append(DELIMITER);
		if (t != null) {
			logger.error(prefixedTitle + ": " + builder.toString(), t);
			builder.append("Stack trace:\n");
			t.printStackTrace(new PrintWriter(builder));
			builder.append(DELIMITER);
		} else {
			logger.error(prefixedTitle + ": " + builder.toString());
		}
		alertService.alert(new Alert(priority, prefixedTitle, builder.toString()));
	}

	public boolean withErrorHandling(Processor processor, Payload payload, Runnable runnable) {
		boolean error = true;
		try {
			try {
				runnable.run();
				error = false;
			} catch (RuntimeProcessorError e) {
				displayRuntimeError(e);
			} catch (ValidationError e) {
				displayValidationError(e);
			} catch (Throwable t) {
				displayRuntimeError(new RuntimeProcessorError(t, processor, payload));
			}
		} catch (Throwable t) {
			logger.error("Error while displaying error", t);
		}
		return error;
	}

	public IncludePath.LoadedFile findFile(String filename, IncludePath currentPath) {
		File file = new File(filename);
		if (file.exists() && file.isFile()) {
			return new IncludePath.LoadedFile(filename, file);
		}
		if (file.isAbsolute()) {
			return null;
		}
		if (!currentPath.isEmpty()) {
			IncludePath.LoadedFile last = currentPath.last();
			String lastParentDir = last.getFile().getParent();
			if (lastParentDir == null) {
				lastParentDir = ".";
			}
			File relative = new File(lastParentDir, filename);
			if (relative.exists() && relative.isFile()) {
				return new IncludePath.LoadedFile(filename, relative);
			}
		}
		for (File directory : descriptorDirectories) {
			File directoryFile = new File(directory, filename);
			if (directoryFile.exists() && directoryFile.isFile()) {
				return new IncludePath.LoadedFile(filename, directoryFile);
			}
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		if (mainProcessor != null) {
			boolean error = withErrorHandling(mainProcessor, null, () -> mainProcessor.stop());
			if (error) {
				logger.error("Cannot stop main processor");
			}
		}
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", profileService.profile().getExecutorTerminationTimeout());
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

	public ConsoleService getConsoleService() {
		return consoleService;
	}

	public HttpService getHttpService() {
		return httpService;
	}

	public ScheduledThreadPoolExecutor getScheduledExecutor() {
		return scheduledExecutor;
	}

	public ThreadPoolExecutor getConsumerExecutor() {
		return consumerExecutor;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public NashornScriptEngine getNashorn() {
		return nashorn;
	}

	public String getHostName() {
		return hostName;
	}

}
