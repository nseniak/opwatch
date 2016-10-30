package com.untrackr.alerter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.AlertData;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.consumer.alert.AlertGenerator;
import com.untrackr.alerter.processor.producer.console.Stdin;
import com.untrackr.alerter.processor.special.pipe.Pipe;
import com.untrackr.alerter.processor.transformer.print.Stdout;
import jdk.nashorn.api.scripting.NashornException;
import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import sun.misc.Signal;

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

import static jdk.nashorn.internal.runtime.ScriptRuntime.UNDEFINED;

@Service
public class ProcessorService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

	@Autowired
	private ProfileService profileService;

	@Autowired
	private AlertService alertService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private FileTailingService fileTailingService;

	@Autowired
	private ConsoleService consoleService;

	@Autowired
	private HttpService httpService;

	@Autowired
	private ApplicationContext applicationContext;

	private String hostName;

	private ObjectMapper objectMapper;

	private Thread mainThread;

	private Processor runningProcessor;

	private ThreadPoolExecutor consumerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
			60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
			ThreadUtil.threadFactory("Consumer"));

	private ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("ScheduledTask"));

	@Override
	public void afterPropertiesSet() throws Exception {
		// Object mapper
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	}

	public void runCommandLine(String[] argStrings) {
		mainThread = Thread.currentThread();
		Signal.handle(new Signal("INT"), sig -> {
			printCtrlC();
			mainThread.interrupt();
		});
		scriptService.initialize();
		try {
			CommandLineOptions options = parseOptions(argStrings);
			initialize(options);
			if (options.getFiles().isEmpty()) {
				runShell(options);
			} else {
				runFiles(options);
			}
		} catch (Exception e) {
			printError(e.getMessage());
		}
	}

	private CommandLineOptions parseOptions(String[] argStrings) {
		OptionParser parser = new OptionParser();
		OptionSpec<String> hostname = parser.accepts("hostname").withRequiredArg().ofType(String.class);
		OptionSpec<File> files = parser.nonOptions().ofType(File.class);
		OptionSet optionSet;
		try {
			optionSet = parser.parse(argStrings);
		} catch (OptionException e) {
			throw new RuntimeException(e);
		}
		CommandLineOptions options = new CommandLineOptions();
		options.setHostname(optionSet.valueOf(hostname));
		options.setFiles(optionSet.valuesOf(files));
		return options;
	}

	private void initialize(CommandLineOptions options) {
		if (options.getHostname() != null) {
			hostName = options.getHostname();
		} else {
			try {
				hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				throw new IllegalStateException("Cannot determine hostname; please pass the option --hostname=<hostname>");
			}
		}
	}

	public void runShell(CommandLineOptions options) {
		ConsoleReader reader;
		try {
			reader = new ConsoleReader();
		} catch (IOException e) {
			printError("Cannot read from console.");
			return;
		}
		reader.setHandleUserInterrupt(true);
		reader.setPrompt("> ");
		String line;
		while ((line = readLine(reader)) != null) {
			if (!line.trim().isEmpty()) {
				scriptService.execute(line);
			}
		}
	}

	public void printError(String message) {
		System.err.println(message);
	}

	public void printMessage(String message) {
		System.out.println(message);
	}

	private void printCtrlC() {
		System.err.print("^C");
	}

	private String readLine(ConsoleReader reader) {
		while (true) {
			try {
				return reader.readLine();
			} catch (IOException e) {
				return null;
			} catch (UserInterruptException e) {
				printCtrlC();
			}
		}
	}

	public Object runProcessor(Object scriptObject) {
		String name = "run";
		Processor processor = (Processor) scriptService.convertScriptValue(ValueLocation.makeArgument(name), Processor.class, scriptObject,
				() -> ExceptionContext.makeProcessorFactory(name));
		Processor wrappedProcessor = wrapToplevelProcessor(processor);
		wrappedProcessor.check();
		wrappedProcessor.start();
		infoAlert("alerter up and running");
		runningProcessor = processor;
		try {
			while (true) {
				Thread.sleep(TimeUnit.DAYS.toMillis(1));
			}
		} catch (InterruptedException e) {
			printError("Processor interrupted");
		} finally {
			runningProcessor = null;
		}
		wrappedProcessor.stop();
		return UNDEFINED;
	}

	public Processor wrapToplevelProcessor(Processor processor) {
		List<Processor> pipeProcessors = new ArrayList<>();
		if (processor.getSignature().getInputRequirement() != ProcessorSignature.PipeRequirement.forbidden) {
			logger.info("Adding \"stdin\" as input");
			pipeProcessors.add(new Stdin(this, "stdin"));
		}
		pipeProcessors.add(processor);
		if (processor.getSignature().getOutputRequirement() != ProcessorSignature.PipeRequirement.forbidden) {
			logger.info("Adding \"stdout\" as output");
			pipeProcessors.add(new Stdout(this, "stdout"));
		}
		if (pipeProcessors.size() == 1) {
			return processor;
		} else {
			return new Pipe(this, pipeProcessors, "pipe");
		}
	}

	private void runFiles(CommandLineOptions options) {
		for (File file : options.getFiles()) {
			scriptService.loadScriptFile(file);
		}
	}

	public void stopRunningProcessor() {
		mainThread.interrupt();
	}

	public void processorAlert(Alert.Priority priority, String title, AlertGenerator emitter) {
		Alert alert = makeProcessorAlert(priority, title, emitter);
		alertService.alert(alert);
	}

	public void processorAlertEnd(Alert.Priority priority, String title, AlertGenerator emitter) {
		Alert alert = makeProcessorAlert(priority, title, emitter);
		alert.setEnd(true);
		alertService.alert(alert);
	}

	public void infoAlert(String title) {
		Alert alert = new Alert(Alert.Priority.info, title, null, null, null);
		alertService.alert(alert);
	}

	private Alert makeProcessorAlert(Alert.Priority priority, String title, AlertGenerator emitter) {
		AlertData data = new AlertData();
		data.add("hostname", getHostName());
		Alert alert = new Alert(priority, title, null, emitter, data);
		return alert;
	}

	public void displayAlerterException(AlerterException e) {
		AlertData data = new AlertData();
		data.add("hostname", getHostName());
		ExceptionContext context = e.getExceptionContext();
		ProcessorLocation processorLocation = context.getProcessorLocation();
		CallbackErrorLocation callbackLocation = context.getCallbackErrorLocation();
		String title = (callbackLocation != null) ? "Scripting error" : "Execution error";
		String message = e.getLocalizedMessage();
		if (processorLocation != null) {
			data.add("processor", processorLocation.descriptor());
		}
		if (callbackLocation != null) {
			String descriptor = callbackLocation.descriptor();
			if (descriptor != null) {
				data.add("stack", descriptor);
			}
		}
		Payload payload = context.getPayload();
		if (payload != null) {
			data.add("payload", payload.asText());
		}
		Throwable cause = e.getCause();
		if ((cause != null) && !((cause instanceof ScriptException) || (cause instanceof IOException) || (cause instanceof NashornException))) {
			addStack(data, e);
			logger.error(title, e);
		}
		if (!e.isSilent()) {
			Alert alert = new Alert(Alert.Priority.emergency, title, message, null, data);
			alertService.alert(alert);
		}
	}

	public void infrastructureAlert(Alert.Priority priority, String title, String details) {
		infrastructureAlert(priority, title, details, null);
	}

	public void infrastructureAlert(Alert.Priority priority, String message, String details, Throwable t) {
		String title = "Internal error";
		AlertData data = new AlertData();
		data.add("hostname", getHostName());
		data.add("details", details);
		String logMessage = message + ": " + details;
		if (t != null) {
			addStack(data, t);
		} else {
			logger.error(logMessage);
		}
		alertService.alert(new Alert(priority, title, message, null, data));
	}

	private void addStack(AlertData data, Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		data.add("stack", writer.toString());
	}

	public boolean withToplevelErrorHandling(Runnable runnable) {
		boolean error = true;
		try {
			runnable.run();
			error = false;
		} catch (AlerterException e) {
			displayAlerterException(e);
		} catch (Throwable t) {
			displayAlerterException(new AlerterException(t, ExceptionContext.makeToplevel()));
		}
		return error;
	}

	public boolean withProcessorErrorHandling(Processor processor, Runnable runnable) {
		boolean error = true;
		try {
			try {
				runnable.run();
				error = false;
			} catch (AlerterException e) {
				displayAlerterException(e);
			} catch (Throwable t) {
				displayAlerterException(new AlerterException(t, ExceptionContext.makeProcessorNoPayload(processor)));
			}
		} catch (Throwable t) {
			logger.error("Error while displaying error", t);
		}
		return error;
	}

	public String valueAsString(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			return "<cannot convert to string>";
		}
	}

	public void exit() {
		SpringApplication.exit(applicationContext);
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Exiting");
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", profileService.profile().getExecutorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "ScheduledExecutor", profileService.profile().getExecutorTerminationTimeout());
	}

	public HealthcheckInfo healthcheck() {
		String runningProcessorDesc = (runningProcessor == null) ? null : runningProcessor.getLocation().descriptor();
		return new HealthcheckInfo(hostName, runningProcessorDesc);
	}

	public AlerterProfile profile() {
		return profileService.profile();
	}

	public AlertService getAlertService() {
		return alertService;
	}

	public ProfileService getProfileService() {
		return profileService;
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

	public String getHostName() {
		return hostName;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

}
