package com.untrackr.alerter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.untrackr.alerter.common.InternalScriptError;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.AlertData;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.producer.console.Console;
import com.untrackr.alerter.processor.special.pipe.Pipe;
import com.untrackr.alerter.processor.transformer.print.Print;
import jdk.nashorn.api.scripting.NashornException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

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

	private FrequencyLimiter frequencyLimiter;

	private ObjectMapper objectMapper;

	private Processor mainProcessor;

	private String mainProcessorFile;

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
		// Global alert frequency limiter
		frequencyLimiter = new FrequencyLimiter(TimeUnit.MINUTES.toMillis(1), profileService.profile().getGlobalMaxAlertsPerMinute());
	}


	public void startMainProcessor() {
		try {
			if (mainProcessor != null) {
				logger.info("Main processor already started");
				return;
			}
			mainProcessorFile = property("alerter.main");
			boolean error = withToplevelErrorHandling(() -> {
				scriptService.initialize();
				mainProcessor = scriptService.loadProcessor(mainProcessorFile);
				if (profileService.profile().isInteractive()) {
					List<Processor> pipeProcessors = new ArrayList<>();
					if (mainProcessor.getSignature().getInputRequirement() != ProcessorSignature.PipeRequirement.forbidden) {
						logger.info("Adding \"console\" as input processor");
						pipeProcessors.add(new Console(this, "console"));
					}
					pipeProcessors.add(mainProcessor);
					if (mainProcessor.getSignature().getOutputRequirement() != ProcessorSignature.PipeRequirement.forbidden) {
						logger.info("Adding \"print\" as output processor");
						pipeProcessors.add(new Print(this, "print"));
					}
					if (pipeProcessors.size() != 1) {
						mainProcessor = new Pipe(this, pipeProcessors, "pipe");
					}
				}
				mainProcessor.check();
				mainProcessor.start();
			});
			if (mainProcessor == null) {
				logger.error("Cannot load main processor: " + mainProcessorFile);
			} else if (error) {
				logger.error("Cannot start main processor: " + mainProcessorFile);
				// Stop those sub-processors that have been started
				mainProcessor.stop();
				mainProcessor = null;
			} else {
				logger.info("Started");
				infoAlert(alertService.getDefaultPushoverKey(), "Alerter up and running on " + getHostName());
			}
		} catch (Throwable t) {
			String message = "Unexpected startup error occurred";
			logger.error(message, t);
			infrastructureAlert(Alert.Priority.emergency, message, "--", t);
		}
	}

	public void stopMainProcessor() {
		if (mainProcessor == null) {
			logger.info("Main processor not started");
			return;
		}
		try {
			boolean error = withProcessorErrorHandling(mainProcessor, mainProcessor::stop);
			if (error) {
				logger.error("Cannot stop main processor");
			}
		} finally {
			logger.info("Stopped");
			mainProcessor = null;
		}
	}

	public void processorAlert(PushoverKey pushoverKey, Alert.Priority priority, String title, Processor emitter) {
		Alert alert = makeProcessorAlert(pushoverKey, priority, title, emitter);
		alertService.alert(alert);
	}

	public void processorAlertEnd(PushoverKey pushoverKey, Alert.Priority priority, String title, Processor emitter) {
		Alert alert = makeProcessorAlert(pushoverKey, priority, title, emitter);
		alert.setEnd(true);
		alertService.alert(alert);
	}

	public void infoAlert(PushoverKey pushoverKey, String title) {
		Alert alert = new Alert(pushoverKey, Alert.Priority.info, title, null, null, null);
		alertService.alert(alert);
	}

	private Alert makeProcessorAlert(PushoverKey pushoverKey, Alert.Priority priority, String title, Processor emitter) {
		AlertData data = new AlertData();
		data.add("hostname", getHostName());
		Alert alert = new Alert(pushoverKey, priority, title, null, emitter, data);
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
		if ((e.getCause() != null) && !((e.getCause() instanceof IOException) || (e.getCause() instanceof NashornException) || (e.getCause() instanceof InternalScriptError))) {
			addStack(data, e);
			logger.error(title, e);
		}
		if (!e.isSilent()) {
			Alert alert = new Alert(alertService.getDefaultPushoverKey(), Alert.Priority.emergency, title, message, null, data);
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
		alertService.alert(new Alert(alertService.getDefaultPushoverKey(), priority, title, message, null, data));
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
		stopMainProcessor();
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", profileService.profile().getExecutorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "ScheduledExecutor", profileService.profile().getExecutorTerminationTimeout());
	}

	public HealthcheckInfo healthcheck() {
		return new HealthcheckInfo(hostName, mainProcessorFile, ((mainProcessor != null) && mainProcessor.started()));
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
