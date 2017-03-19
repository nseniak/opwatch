package com.untrackr.alerter.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.untrackr.alerter.alert.Alert;
import com.untrackr.alerter.alert.AlertData;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.alert.AlertGenerator;
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

	private static String SCRIPT_EXCEPTION_MESSAGE_PREFIX = "javax.script.ScriptException: ";

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
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Exiting");
		mainThread.interrupt();
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", profileService.profile().getExecutorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "ScheduledExecutor", profileService.profile().getExecutorTerminationTimeout());
	}

	public void runCommandLine(String[] argStrings) {
		mainThread = Thread.currentThread();
		Signal.handle(new Signal("INT"), this::userInterruptHandler);
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

	private void userInterruptHandler(Signal signal) {
		printCtrlC();
		mainThread.interrupt();
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
				scriptService.executeConsoleInput(line);
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
		Processor processor = (Processor) scriptService.convertScriptValue(ValueLocation.makeArgument(name, "processor"), Processor.class, scriptObject,
				() -> ExceptionContext.makeProcessorFactory(name));
		processor.inferSignature();
		processor.check();
		processor.start();
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
		// TODO Make sure this is called!
		infoAlert("alerter stopped");
		processor.stop();
		return UNDEFINED;
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
		AlerterException rootException = e;
		while ((rootException.getCause() != null) && (rootException.getCause() instanceof AlerterException)) {
			rootException = (AlerterException) rootException.getCause();
		}
		AlertData data = new AlertData();
		data.add("hostname", getHostName());
		ExceptionContext context = rootException.getExceptionContext();
		ProcessorLocation processorLocation = context.getProcessorLocation();
		CallbackErrorLocation callbackLocation = context.getCallbackErrorLocation();
		String title = (callbackLocation != null) ? "Scripting error" : "Execution error";
		String message = rootException.getLocalizedMessage();
		if (message.startsWith(SCRIPT_EXCEPTION_MESSAGE_PREFIX)) {
			// Script exception messages contain the exception name, which is ugly
			message = message.substring(SCRIPT_EXCEPTION_MESSAGE_PREFIX.length());
		}
		if (processorLocation != null) {
			data.add("processor", processorLocation.descriptor());
			message = processorLocation.getName() + ": " + message;
		}
		if (callbackLocation != null) {
			String descriptor = callbackLocation.descriptor();
			if (descriptor != null) {
				data.add("stack", descriptor);
			}
		}
		Payload payload = context.getPayload();
		if (payload != null) {
			data.add("payload", json(payload));
		}
		Throwable cause = rootException.getCause();
		if ((cause != null) && !((cause instanceof ScriptException) || (cause instanceof IOException) || (cause instanceof NashornException))) {
			addStack(data, rootException);
			logger.error(title, rootException);
		}
		if (!rootException.isSilent()) {
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
			} catch (Throwable t) {
				displayAlerterException(new AlerterException(t, ExceptionContext.makeProcessorNoPayload(processor)));
			}
		} catch (Throwable t) {
			logger.error("Error while displaying error", t);
		}
		return error;
	}

	public String json(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			return "<cannot convert to string>";
		}
	}

	public Object parseJson(String text) {
		try {
			return objectMapper.readValue(text, Object.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void exit() {
		SpringApplication.exit(applicationContext);
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
