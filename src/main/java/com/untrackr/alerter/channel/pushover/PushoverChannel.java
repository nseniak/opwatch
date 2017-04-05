package com.untrackr.alerter.channel.pushover;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.common.FrequencyLimiter;
import com.untrackr.alerter.processor.common.GlobalExecutionContext;
import com.untrackr.alerter.processor.common.Message;
import com.untrackr.alerter.processor.common.MessageScope;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.service.ProcessorService;
import net.pushover.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PushoverChannel implements Channel {

	private static final Logger logger = LoggerFactory.getLogger(PushoverChannel.class);

	public static int MAX_TITLE_LENGTH = 250;
	public static int MAX_MESSAGE_LENGTH = 1024;
	private static final int MAX_DATA_ITEM_LENGTH = 120;
	private static final String FIELD_DELIMITER = "\n--\n";

	private String name;
	private PushoverConfiguration config;
	private PushoverMessageService service;
	private ProcessorService processorService;
	private PushoverKey pushoverKey;
	private Map<String, FrequencyLimiter> scopeFrequenceyLimiter = new ConcurrentHashMap<>();
	private FrequencyLimiter globalFrequencyLimiter;
	private int globalAlertCount;

	public PushoverChannel(String name, PushoverConfiguration config, PushoverMessageService service, ProcessorService processorService) {
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		this.pushoverKey = makeKey(name);
		initializeFrequencyLimiters();
	}

	@Override
	public String serviceName() {
		return service.serviceName();
	}

	private PushoverKey makeKey(String channelName) {
		if ((config.getChannels() == null) || (config.getChannels().get(channelName) == null)) {
			throw new RuntimeError("Pushover channel configuration not found: " + channelName);
		}
		PushoverConfiguration.ChannelConfig channelConfig = config.getChannels().get(channelName);
		String apiToken = channelConfig.getApiToken();
		if (apiToken == null) {
			throw new RuntimeError("Pushover apiToken not defined for channel \"" + channelName + "\"");
		}
		String userKey = channelConfig.getUserKey();
		if (userKey == null) {
			throw new RuntimeError("Pushover userKey not defined for channel \"" + channelName + "\"");
		}
		return new PushoverKey(apiToken, userKey);
	}

	private void initializeFrequencyLimiters() {
		scopeFrequenceyLimiter = new ConcurrentHashMap<>();
		int maxAlertsPerMinute = config.getGlobalMaxPerMinute();
		globalFrequencyLimiter = new FrequencyLimiter(TimeUnit.MINUTES.toMillis(1), maxAlertsPerMinute);
		globalAlertCount = 0;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void publish(Message message) {
		globalAlertCount = globalAlertCount + 1;
		MessagePriority priority = levelPriority(message.getLevel());
		String title = truncate(alertTitle(message), MAX_TITLE_LENGTH);
		StringWriter writer = new StringWriter();
		MessageScope scope = message.getScope();
		writer.append("Hostname: " + scope.getHostname());
		writer.append("\n");
		if (message.getBody() != null) {
			writer.append(message.getBody()).append(FIELD_DELIMITER);
		}
		if (message.getData() != null) {
			for (Map.Entry<String, String> pair : message.getData().entrySet()) {
				String key = pair.getKey();
				String value = pair.getValue();
				writer.append(key).append(": ").append(truncate(value, MAX_DATA_ITEM_LENGTH)).append(FIELD_DELIMITER);
			}
		}
		String body = writer.toString();
		if (body.isEmpty()) {
			body = "--";
		}
		body = truncate(body, MAX_MESSAGE_LENGTH);
		Integer retry = null;
		Integer expire = null;
		if (message.getLevel() == Message.Level.emergency) {
			retry = config.getEmergencyRetry();
			expire = config.getEmergencyExpire();
		}
		if (checkFrequencyLimits(scope)) {
			return;
		}
		PushoverMessage pushoverMessage = makePushoverMessage(pushoverKey, title, body, priority, retry, expire);
		send(pushoverMessage);
	}

	private MessagePriority levelPriority(Message.Level level) {
		switch (level) {
			case lowest:
				return MessagePriority.LOWEST;
			case low:
				return MessagePriority.LOW;
			case medium:
				return MessagePriority.NORMAL;
			case high:
				return MessagePriority.HIGH;
			case emergency:
				return MessagePriority.EMERGENCY;
		}
		throw new IllegalStateException("unknown level: " + level.name());
	}

	private String alertTitle(Message message) {
		return levelPrefix(message.getLevel()) + typeSuffix(message.getType()) + ": " + message.getTitle();
	}

	private String typeSuffix(Message.Type type) {
		switch (type) {
			case error:
				return "Error";
			case info:
				return "Info";
			case alert:
				return "Alert";
			case alertStart:
				return "Alert";
			case alertEnd:
				return "End of Alert";
		}
		throw new IllegalStateException("unknown message type: " + type.name());
	}

	private String levelPrefix(Message.Level level) {
		switch (level) {
			case lowest:
			case low:
			case medium:
				return "";
			case high:
				return "High ";
			case emergency:
				return "EMERGENCY ";
		}
		throw new IllegalStateException("unknown level: " + level.name());
	}

	private boolean checkFrequencyLimits(MessageScope scope) {
		String scopeId = scope.getId();
		FrequencyLimiter scopeLimiter = scopeFrequenceyLimiter.get(scopeId);
		if (scopeLimiter == null) {
			int maxAlertsPerMinute = config.getMaxPerMinute();
			scopeLimiter = new FrequencyLimiter(TimeUnit.MINUTES.toMillis(1), maxAlertsPerMinute);
			scopeFrequenceyLimiter.put(scopeId, scopeLimiter);
		}
		if (checkFrequencyLimits(scopeLimiter, "Limit of %1$s reached for " + scope.descriptor())) {
			return true;
		}
		if (checkFrequencyLimits(globalFrequencyLimiter, "Global limit of %1$s reached")) {
			return true;
		}
		return false;
	}

	private boolean checkFrequencyLimits(FrequencyLimiter limiter, String titleFormat) {
		int scopeEverflow = limiter.ping();
		if (scopeEverflow > 0) {
			if (scopeEverflow == 1) {
				String title = String.format(titleFormat, limiter.describeLimit("message", "messages"));
				String body = "Muting for a moment.";
				PushoverMessage pushoverMessage = makePushoverMessage(pushoverKey, "Alert not sent: " + title, body, MessagePriority.NORMAL, null, null);
				send(pushoverMessage);
			}
			return true;
		}
		return false;
	}

	private String truncate(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, Math.max(0, length - 3)) + "...";
		}
	}

	private PushoverMessage makePushoverMessage(PushoverKey pushoverKey,
																							String title,
																							String message,
																							MessagePriority priority,
																							Integer retry,
																							Integer expire) {
		return PushoverMessage.builderWithApiToken(pushoverKey.getApiToken())
				.setUserId(pushoverKey.getUserKey())
				.setTitle(title)
				.setMessage(message)
				.setPriority(priority)
				.setRetry(retry)
				.setExpire(expire)
				.build();
	}

	private void send(PushoverMessage pushoverMessage) {
		String logMessage = "Message to Pushover[" + name + "] " + processorService.prettyJson(pushoverMessage);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout("(debug) " + logMessage);
			return;
		}
		PushoverClient pushoverClient = new PushoverRestClient();
		try {
			Status result = pushoverClient.pushMessage(pushoverMessage);
			if (result.getStatus() != 1) {
				throw new RuntimeError("Status error while pushing to Pushover: " + result.toString());
			}
		} catch (PushoverException e) {
			throw new RuntimeError("Error while pushing to Pushover" , e, new GlobalExecutionContext());
		}
	}

}
