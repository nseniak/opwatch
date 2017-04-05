package com.untrackr.alerter.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.channel.MessageServiceService;
import com.untrackr.alerter.channel.common.*;
import com.untrackr.alerter.channel.console.ConsoleConfiguration;
import com.untrackr.alerter.channel.console.ConsoleMessageService;
import com.untrackr.alerter.channel.pushover.PushoverMessageService;
import com.untrackr.alerter.common.ThreadUtil;
import com.untrackr.alerter.ioservice.FileTailingService;
import com.untrackr.alerter.processor.common.*;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;
import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_CHANNEL_NAME;
import static jdk.nashorn.internal.runtime.ScriptRuntime.UNDEFINED;

@Service
public class ProcessorService implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorService.class);

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

	@Autowired
	private MessageServiceService messageServiceService;

	private static String DEFAULT_DEFAULT_CHANNEL = "console";

	private String id;
	private AlerterConfig config;
	private Channels channels;
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
		id = uuid();
		// Object mapper
		objectMapper = new ObjectMapper();
		objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(WRITE_NULL_MAP_VALUES, false);
		objectMapper.configure(ALLOW_COMMENTS, true);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Exiting");
		mainThread.interrupt();
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", config().executorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(scheduledExecutor, "ScheduledExecutor", config().executorTerminationTimeout());
	}

	public void run(String[] argStrings) {
		mainThread = Thread.currentThread();
		Signal.handle(new Signal("INT"), this::userInterruptHandler);
		try {
			CommandLineOptions options = parseOptions(argStrings);
			createConfig(options);
			initializeChannels(new ChannelConfig());
			printStdout("Default channel: " + channels.getDefaultChannel().name());
			printStdout("Error channel: " + channels.getErrorChannel().name());
			scriptService.initialize();
			if (options.getFiles().isEmpty()) {
				runRepl(options);
			} else {
				runFiles(options);
			}
		} catch (Exception e) {
			logger.error("Initialization failed", e);
			printStderr("Initialization failed: " + e.getMessage());
		}
	}

	public void initializeChannels(ChannelConfig channelConfig) {
		LinkedHashMap<String, Channel> channelMap = new LinkedHashMap<>();
		addServiceChannels(channelConfig, channelMap, new ConsoleMessageService());
		addServiceChannels(channelConfig, channelMap, new PushoverMessageService());
		Channel consoleChannel = channelMap.computeIfAbsent(CONSOLE_CHANNEL_NAME,
				k -> new ConsoleMessageService().createChannels(new ConsoleConfiguration(), this).get(0));
		Channel defaultChannel = null;
		Channel errorChannel = null;
		String defaultChannelName = channelConfig.getDefaultChannel();
		if (defaultChannelName != null) {
			defaultChannel = channelMap.get(defaultChannelName);
			if (defaultChannel == null) {
				throw new RuntimeError("the specified default channel does not exist: \"" + defaultChannelName + "\"");
			}
		} else {
			defaultChannel = consoleChannel;
		}
		String errorChannelName = channelConfig.getErrorChannel();
		if (errorChannelName != null) {
			errorChannel = channelMap.get(errorChannelName);
			if (errorChannel == null) {
				throw new RuntimeError("the specified error channel does not exist: \"" + errorChannelName + "\"");
			}
		} else {
			errorChannel = defaultChannel;
		}
		channels = new Channels(channelMap, defaultChannel, errorChannel);
		logger.info("Default channel: " + channels.getDefaultChannel().name());
		logger.info("Error channel: " + channels.getErrorChannel().name());
	}

	private <F extends ServiceConfiguration, C extends Channel> void addServiceChannels(ChannelConfig channelConfig,
																																											LinkedHashMap<String, Channel> channelMap,
																																											MessageService<F, C> service) {
		String serviceName = service.serviceName();
		Object serviceConfig = channelConfig.getServices().get(serviceName);
		if (serviceConfig == null) {
			return;
		}
		Class<F> configClass = service.configurationClass();
		F config;
		try {
			config = objectMapper.convertValue(serviceConfig, configClass);
		} catch (RuntimeException e) {
			String message = "invalid configuration for service \"" + serviceName + "\"";
			logger.error(message, e);
			throw new RuntimeError(message + ": " + e.getMessage());
		}
		List<C> channels = service.createChannels(config, this);
		for (C channel : channels) {
			String name = channel.name();
			Channel previous = channelMap.put(name, channel);
			if (previous != null) {
				throw new RuntimeError("cannot create " + channel.serviceName() + " channel \"" + name + "\": a channel with the same name already exists");
			}
		}
	}

	private void createConfig(CommandLineOptions options) {
		config = new AlerterConfig(this, options);
		if (config.hostName() == null) {
			try {
				config.hostName(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				throw new RuntimeError("Cannot determine hostname; please pass the option --hostname=<hostname>");
			}
		}
	}

	public Channel findChannel(String name) {
		return channels.getChannelMap().get(name);
	}

	public Channel consoleChannel() {
		return findChannel(CONSOLE_CHANNEL_NAME);
	}

	public Channel defaultChannel() {
		return channels.getDefaultChannel();
	}

	public Channel errorChannel() {
		return channels.getErrorChannel();
	}

	private void userInterruptHandler(Signal signal) {
		printCtrlC();
		mainThread.interrupt();
	}

	private CommandLineOptions parseOptions(String[] argStrings) {
		OptionParser parser = new OptionParser();
		OptionSpec<String> hostname = parser.accepts("hostname").withRequiredArg().ofType(String.class);
		OptionSpec<String> startup = parser.accepts("startup").withRequiredArg().ofType(String.class);
		OptionSpec<File> files = parser.nonOptions().ofType(File.class);
		OptionSet optionSet;
		try {
			optionSet = parser.parse(argStrings);
		} catch (OptionException e) {
			throw new RuntimeError("cannot parse options: " + e.getMessage(), e);
		}
		CommandLineOptions options = new CommandLineOptions();
		options.setHostname(optionSet.valueOf(hostname));
		options.setStartup(optionSet.valueOf(startup));
		options.setFiles(optionSet.valuesOf(files));
		return options;
	}

	public void runRepl(CommandLineOptions options) {
		ConsoleReader reader;
		try {
			reader = new ConsoleReader();
		} catch (IOException e) {
			printStderr("Cannot read from console: " + e.getMessage());
			return;
		}
		reader.setExpandEvents(false);
		reader.setHandleUserInterrupt(true);
		reader.setPrompt("> ");
		String line;
		while ((line = readLine(reader)) != null) {
			if (!line.trim().isEmpty()) {
				scriptService.executeConsoleInput(line);
			}
		}
	}

	public void printStderr(String message) {
		System.err.println(message);
	}

	public void printStdout(String message) {
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
				(message) -> new RuntimeError(message));
		processor.inferSignature();
		processor.check();
		processor.start();
		signalInfo("alerter up and running");
		runningProcessor = processor;
		try {
			while (true) {
				Thread.sleep(TimeUnit.DAYS.toMillis(1));
			}
		} catch (InterruptedException e) {
			printStderr("Processor interrupted");
		} finally {
			runningProcessor = null;
		}
		signalInfo("alerter stopped");
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

	public void signalInfo(String title) {
		ExecutionContext context = new GlobalExecutionContext();
		MessageScope scope = context.makeMessageScope(this);
		Message message = new Message(Message.Type.info, Message.Level.medium, title, null, scope, null);
		publish(defaultChannel(), message);
	}

	public void signalException(RuntimeError e) {
		String title = e.getMessage();
		if (title.startsWith(SCRIPT_EXCEPTION_MESSAGE_PREFIX)) {
			// Script exception messages contain the exception name, which is ugly
			title = title.substring(SCRIPT_EXCEPTION_MESSAGE_PREFIX.length());
		}
		String emitterName = e.getContext().emitterName();
		if (emitterName != null) {
			title = emitterName + ": " + title;
		}
		ExecutionContext context = e.getContext();
		MessageScope scope = context.makeMessageScope(this);
		MessageData messageData = new MessageData();
		ScriptStack stack = new ScriptStack(e);
		if (!stack.empty()) {
			messageData.put("stack", stack.asString());
		}
		context.addContextData(messageData, this);
		Message message = new Message(Message.Type.error, e.getLevel(), title, null, scope, messageData);
		publish(errorChannel(), message);
	}

	public void publish(Channel channel, Message message) {
		try {
			channel.publish(message);
		} catch (Throwable t) {
			String logMessage = "Error trying to publish to channel \"" + channel.name() + "\": " + t.getMessage();
			logger.error(logMessage, t);
			try {
				printStdout(logMessage);
				printStdout("Publishing to console instead:");
				consoleChannel().publish(message);
			} catch (Throwable t2) {
				logger.error("Error trying to publish to console", t2);
			}
		}
	}

	private void addStack(MessageData data, Throwable t) {
		StringWriter writer = new StringWriter();
		t.printStackTrace(new PrintWriter(writer));
		data.put("stack", writer.toString());
	}

	public boolean withExceptionHandling(String messagePrefix, ExecutionContext context, ThrowingRunnable runnable) {
		boolean error = true;
		try {
			runnable.run();
			error = false;
		} catch (InterruptedException e) {
			// The application is exiting; rethrow
			throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
		} catch (ApplicationInterruptedException e) {
			// The application is exiting; rethrow
			throw e;
		} catch (RuntimeError e) {
			signalException(e);
		} catch (Throwable t) {
			String message = ((messagePrefix != null) ? messagePrefix + ": " : "") + t.getMessage();
			signalException(new RuntimeError(message, t, context));
		}
		return error;
	}

	public interface ThrowingRunnable {

		void run() throws Throwable;

	}

	public String uuid() {
		return UUID.randomUUID().toString();
	}

	public String json(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			return "<cannot convert to string>";
		}
	}

	public String prettyJson(Object value) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			return "<cannot convert to string>";
		}
	}

	public Object parseJson(String text) throws IOException {
		return objectMapper.readValue(text, Object.class);
	}

	public void exit() {
		SpringApplication.exit(applicationContext);
	}

	public HealthcheckInfo healthcheck() {
		String runningProcessorDesc = (runningProcessor == null) ? null : runningProcessor.getLocation().descriptor();
		return new HealthcheckInfo(config.hostName(), runningProcessorDesc);
	}

	public String getId() {
		return id;
	}

	public AlerterConfig config() {
		return config;
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

	public ScriptService getScriptService() {
		return scriptService;
	}

}
