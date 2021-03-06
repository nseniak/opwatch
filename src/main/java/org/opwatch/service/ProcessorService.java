/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jdk.nashorn.internal.runtime.Undefined;
import jline.console.ConsoleReader;
import jline.console.UserInterruptException;
import org.opwatch.Application;
import org.opwatch.CommandLineOptions;
import org.opwatch.channel.common.Channel;
import org.opwatch.channel.common.ChannelConfig;
import org.opwatch.channel.common.MessagingService;
import org.opwatch.common.ObjectMapperService;
import org.opwatch.common.ThreadUtil;
import org.opwatch.ioservice.FileTailingService;
import org.opwatch.processor.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import sun.misc.Signal;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	private ObjectMapperService objectMapperService;

	@Autowired
	private MessagingService messagingService;

	private Integer port;

	private String id;
	private Config config;
	private Thread runningProcessorThread;
	private Object processorReturnValue = UNDEFINED;
	private RestTemplate restTemplate = new RestTemplate();

	private static String SCRIPT_EXCEPTION_MESSAGE_PREFIX = "javax.script.ScriptException: ";

	public static Message.Level INFO_DEFAULT_MESSAGE_LEVEL = Message.Level.medium;

	private ThreadPoolExecutor consumerExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
			60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
			ThreadUtil.threadFactory("ConsumerTask"));

	private ScheduledThreadPoolExecutor producerScheduledExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtil.threadFactory("ScheduledProducerTask"));

	@Override
	public void afterPropertiesSet() throws Exception {
		id = uuid();
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Exiting");
		stopRunningProcessor();
		ThreadUtil.safeExecutorShutdownNow(consumerExecutor, "ConsumerExecutor", config().executorTerminationTimeout());
		ThreadUtil.safeExecutorShutdownNow(producerScheduledExecutor, "ScheduledProducerExecutor", config().executorTerminationTimeout());
	}

	public boolean initialize(CommandLineOptions options) {
		Signal.handle(new Signal("INT"), this::userInterruptHandler);
		try {
			port = (options.isNoServer()) ? null
					: (options.getPort() != null) ? options.getPort()
					: Application.DEFAULT_HTTP_PORT;
			createConfig(options);
			messagingService.initializeChannels(new ChannelConfig());
			scriptService.initialize();
			return true;
		} catch (Exception e) {
			logger.error("Initialization failed", e);
			printStderr("Initialization failed: " + exceptionMessage(e));
			ScriptStack stack = ScriptStack.exceptionStack(e);
			if (!stack.empty()) {
				printStderr(stack.asString());
			}
			return false;
		}
	}

	public boolean run(CommandLineOptions options) {
		if (!initialize(options)) {
			return false;
		}
		return withExceptionHandling(null, GlobalExecutionScope::new, () -> {
			if (options.getRunExpression() != null) {
				runExpression(options);
			} else if (options.getScripts().isEmpty()) {
				runRepl(options);
			} else {
				runFiles(options);
			}
		});
	}

	private void userInterruptHandler(Signal signal) {
		printCtrlC();
		stopRunningProcessor();
	}

	private void createConfig(CommandLineOptions options) {
		config = new Config(this, options);
		if (config.hostName() == null) {
			try {
				config.hostName(InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				throw new RuntimeError("Cannot determine hostname; please specify one with -hostname <hostname>");
			}
		}
	}

	private Object runExpression(CommandLineOptions options) {
		return scriptService.runExpression(options.getRunExpression());
	}

	public void runRepl(CommandLineOptions options) {
		ConsoleReader reader;
		try {
			reader = new ConsoleReader();
		} catch (IOException e) {
			printStderr("Cannot read from console: " + exceptionMessage(e));
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
		System.err.flush();
	}

	public void printStdout(String message) {
		System.out.println(message);
		System.out.flush();
	}

	private void printCtrlC() {
		System.err.print("^C");
		System.err.flush();
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

	private void runFiles(CommandLineOptions options) {
		for (String file : options.getScripts()) {
			scriptService.loadScript(file);
		}
	}

	public void stopWithReturnValue(Object value) {
		processorReturnValue = value;
		stopRunningProcessor();
	}

	public void stopRunningProcessor() {
		if (runningProcessorThread != null) {
			try {
				runningProcessorThread.interrupt();
			} catch (NullPointerException e) {
				// The processor was stopped concurrently. Nothing to do.
			}
		}
	}

	public boolean running() {
		return runningProcessorThread != null;
	}

	public void signalSystemInfo(String title) {
		ExecutionScope scope = new GlobalExecutionScope();
		MessageContext context = scope.makeContext(this, ScriptStack.currentStack());
		Message message = Message.makeNew(Message.Type.info, INFO_DEFAULT_MESSAGE_LEVEL, title, null, context);
		publish(messagingService.systemChannel(), message);
	}

	public void signalSystemException(RuntimeError e) {
		logger.info("Error occurred", e);
		String title = exceptionMessage(e);
		if (title.startsWith(SCRIPT_EXCEPTION_MESSAGE_PREFIX)) {
			// Script exception messages contain the exception name, which is ugly
			title = title.substring(SCRIPT_EXCEPTION_MESSAGE_PREFIX.length());
		}
		ExecutionScope scope = e.getScope();
		ScriptStack stack = ScriptStack.exceptionStack(e);
		MessageContext context = scope.makeContext(this, stack);
		String processorName = context.getProcessorName();
		if (processorName != null) {
			title = processorName + ": " + title;
		}
		Message message = Message.makeNew(Message.Type.error, e.getLevel(), title, stack.asStringOrNull(), context);
		publish(messagingService.systemChannel(), message);
	}

	public void publish(Channel channel, Message message) {
		try {
			logger.info("Publishing to " + channel.logString() + ": " + prettyJson(message));
			channel.publish(message);
		} catch (Throwable t) {
			try {
				String logMessage = "Error trying to publish to channel " + channel.logString() + ": " + exceptionMessage(t);
				logger.error(logMessage, t);
				printStdout(logMessage);
				printStdout("Publishing to fallback channel instead: " + messagingService.fallbackChannel().logString());
				messagingService.fallbackChannel().publish(message);
			} catch (Throwable t2) {
				try {
					String logMessage = "Error trying to publish to the fallback channel " + messagingService.fallbackChannel() + ": " + exceptionMessage(t);
					logger.error(logMessage, t2);
					printStdout(logMessage);
					printStdout("Publishing to default console channel");
					messagingService.defaultConsoleChannel().publish(message);
				} catch (Throwable t3) {
					logger.error("Error trying to publish to the console channel", t3);
				}
			}
		}
	}

	public boolean withExceptionHandling(String messagePrefix, ExecutionScopeFactory defaultScopeFactory, ThrowingRunnable runnable) {
		boolean success = false;
		try {
			runnable.run();
			success = true;
		} catch (InterruptedException e) {
			// The application is exiting; rethrow
			throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
		} catch (ApplicationInterruptedException e) {
			// The application is exiting; rethrow
			throw e;
		} catch (RuntimeError e) {
			signalSystemException(e);
		} catch (Throwable t) {
			String message = ((messagePrefix != null) ? messagePrefix + ": " : "") + exceptionMessage(t);
			signalSystemException(new RuntimeError(message, defaultScopeFactory.make(), t));
		}
		return success;
	}

	public <T> ResponseEntity<T> postForEntityWithErrors(String uri,
																											 Object request,
																											 Class<T> clazz,
																											 String hostname,
																											 int port,
																											 String path,
																											 ExecutionScopeFactory scopeFactory) {
		try {
			return restTemplate.postForEntity(uri, request, clazz);
		} catch (ResourceAccessException e) {
			throw new RuntimeError("cannot connect to " + hostname + ":" + port,
					scopeFactory.make());
		} catch (HttpStatusCodeException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.NOT_FOUND) {
				throw new RuntimeError("no receiver for path \"" + path + "\" found on " + hostname + ":" + port,
						scopeFactory.make());
			}
			String errorMessage = e.getResponseBodyAsString();
			if (errorMessage.isEmpty()) {
				errorMessage = "bad status: " + status.value() + " " + status.getReasonPhrase();
			}
			throw new RuntimeError("invalid response status when posting to " + uri + ": " + errorMessage,
					scopeFactory.make());
		}
	}

	private String exceptionMessage(Throwable t) {
		return (t.getMessage() != null) ? t.getMessage() : t.getClass().getName();
	}

	public interface ExecutionScopeFactory {

		ExecutionScope make();

	}

	public interface ThrowingRunnable {

		void run() throws Throwable;

	}

	public String uuid() {
		return UUID.randomUUID().toString();
	}

	public String json(Object value) {
		try {
			return objectMapperService.objectMapper().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("Error while converting json to string", e);
			return "<cannot convert to string>";
		}
	}

	public String prettyJson(Object value) {
		try {
			return objectMapperService.objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("Error while converting json to string", e);
			return "<cannot convert to string>";
		}
	}

	public Object parseJson(String text) throws IOException {
		return objectMapperService.objectMapper().readValue(text, Object.class);
	}

	public void exit() {
		SpringApplication.exit(applicationContext);
	}

	public Object getProcessorReturnValue() {
		return processorReturnValue;
	}

	public void setProcessorReturnValue(Undefined processorReturnValue) {
		this.processorReturnValue = processorReturnValue;
	}

	public HealthcheckInfo healthcheck() {
		return new HealthcheckInfo(config.hostName(), running());
	}

	public String getId() {
		return id;
	}

	public Config config() {
		return config;
	}

	public String hostName() {
		return config.hostName();
	}

	public Integer port() {
		return port;
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

	public ScheduledThreadPoolExecutor getProducerScheduledExecutor() {
		return producerScheduledExecutor;
	}

	public ThreadPoolExecutor getConsumerExecutor() {
		return consumerExecutor;
	}

	public ObjectMapperService getObjectMapperService() {
		return objectMapperService;
	}

	public ScriptService getScriptService() {
		return scriptService;
	}

	public MessagingService getMessagingService() {
		return messagingService;
	}

	public void setRunningProcessorThread(Thread runningProcessorThread) {
		this.runningProcessorThread = runningProcessorThread;
	}

}
