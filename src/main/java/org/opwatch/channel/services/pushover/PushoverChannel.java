package org.opwatch.channel.services.pushover;

import net.pushover.client.*;
import org.opwatch.channel.common.throttled.MessageAggregate;
import org.opwatch.channel.common.throttled.Rate;
import org.opwatch.channel.common.throttled.RateLimiter;
import org.opwatch.channel.common.throttled.ThrottledChannel;
import org.opwatch.processor.common.GlobalExecutionScope;
import org.opwatch.processor.common.Message;
import org.opwatch.processor.common.MessageContext;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PushoverChannel extends ThrottledChannel<PushoverConfiguration> {

	private static final Logger logger = LoggerFactory.getLogger(PushoverChannel.class);

	public static int MAX_TITLE_LENGTH = 250;
	public static int MAX_MESSAGE_LENGTH = 1024;

	private String name;
	private PushoverConfiguration config;
	private PushoverMessageService service;
	private RateLimiter rateLimiter;
	private PushoverKey pushoverKey;

	public PushoverChannel(String name, PushoverConfiguration config, PushoverMessageService service, ProcessorService processorService) {
		super(processorService, service);
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		this.pushoverKey = makeKey(name);
		this.rateLimiter = new RateLimiter(service.timestampSeconds(),
				Arrays.asList(new Rate((int) TimeUnit.MINUTES.toSeconds(1), config.getMaxPerMinute())),
				service.apiTokenRateLimit(pushoverKey.getApiToken()));
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

	@Override
	public String name() {
		return name;
	}

	@Override
	protected RateLimiter rateLimiter() {
		return rateLimiter;
	}

	@Override
	protected void publishLimitReached(Rate rateLimit) {
		String title = limitReachedMessage(rateLimit);
		String body = "Muting for a while";
		PushoverMessage pushoverMessage = makePushoverMessage(pushoverKey, title, body, MessagePriority.NORMAL);
		send(pushoverMessage);
	}

	@Override
	protected void publishAggregate(List<Message> messages, Date mutedOn) {
		MessageAggregate aggregate = new MessageAggregate();
		for (Message message : messages) {
			aggregate.addMessage(displayTitle(message), message);
		}
		Message.Level level = aggregate.getMaxLevel();
		String title = "Unmuting" + levelSuffix(level) + " " + processorService.hostName();
		StringWriter writer = new StringWriter();
		writer.append("The following messages were muted:").append("\n");
		for (MessageAggregate.AggregateMessagePart part : aggregate.messageParts()) {
			writer.append(part.titleWithCount()).append("\n");
		}
		String body = truncate(writer.toString(), MAX_MESSAGE_LENGTH);
		PushoverMessage pushoverMessage = makePushoverMessage(pushoverKey, title, body, levelPriority(level));
		send(pushoverMessage);
	}

	@Override
	protected void publishOne(Message message) {
		MessagePriority priority = levelPriority(message.getLevel());
		String title = truncate(displayTitle(message), MAX_TITLE_LENGTH);
		StringWriter writer = new StringWriter();
		MessageContext scope = message.getContext();
		writer.append("Hostname: " + scope.getHostname()).append("\n");
		writeDetails(writer, message);
		String bodyText = writer.toString();
		bodyText = truncate(bodyText, MAX_MESSAGE_LENGTH);
		PushoverMessage pushoverMessage = makePushoverMessage(pushoverKey, title, bodyText, priority);
		send(pushoverMessage);
	}

	private void writeDetails(StringWriter writer, Message message) {
		String detailsPrefix = (message.getType().isSystem()) ? "" : "Details: ";
		String detailsString = detailsString(message);
		if (detailsString != null) {
			writer.append(detailsPrefix + detailsString);
		}
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
																							MessagePriority priority) {
		Integer retry = null;
		Integer expire = null;
		if (priority == MessagePriority.EMERGENCY) {
			retry = config.getEmergencyRetry();
			expire = config.getEmergencyExpire();
		}
		return PushoverMessage.builderWithApiToken(pushoverKey.getApiToken())
				.setUserId(pushoverKey.getUserKey())
				.setTitle(title)
				.setMessage(message)
				.setPriority(priority)
//				.setRetry(retry)
//				.setExpire(expire)
				.build();
	}

	private void send(PushoverMessage pushoverMessage) {
		String logMessage = logString() + " " + processorService.prettyJson(pushoverMessage);
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
			throw new RuntimeError("Error while pushing to Pushover", new GlobalExecutionScope(), e);
		}
	}

}
