package com.untrackr.alerter.channel.services.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Color;
import allbegray.slack.type.Field;
import allbegray.slack.type.Payload;
import allbegray.slack.webhook.SlackWebhookClient;
import com.untrackr.alerter.channel.common.throttled.MessageAggregate;
import com.untrackr.alerter.channel.common.throttled.Rate;
import com.untrackr.alerter.channel.common.throttled.ThrottledChannel;
import com.untrackr.alerter.channel.common.throttled.RateLimiter;
import com.untrackr.alerter.processor.common.Message;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.StringUtils.capitalize;

public class SlackChannel extends ThrottledChannel<SlackConfiguration> {

	private static final Logger logger = LoggerFactory.getLogger(SlackChannel.class);

	private String name;
	private SlackConfiguration config;
	private SlackMessageService service;
	private ProcessorService processorService;
	private String webhookUrl;
	private SlackWebhookClient client;
	private RateLimiter rateLimiter;

	public SlackChannel(String name, SlackConfiguration config, SlackMessageService service, ProcessorService processorService) {
		super(service);
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		this.webhookUrl = findWebhookUrl(name);
		this.client = SlackClientFactory.createWebhookClient(webhookUrl);
		Rate limit = new Rate((int) TimeUnit.MINUTES.toSeconds(1), config.getMaxPerMinute());
		this.rateLimiter = new RateLimiter(service.timestampSeconds(), Arrays.asList(limit), null);
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
	protected void publishAggregate(List<Message> messages, Date mutedOn) {
		MessageAggregate aggregate = new MessageAggregate();
		for (Message message : messages) {
			String title = displayTitle(message);
			aggregate.addMessage(title, message);
		}
		Payload payload = new Payload();
		payload.setText("Unmuting " + processorService.hostName() + ". The following messages were muted:");
		Attachment attachment = new Attachment();
		StringWriter writer = new StringWriter();
		boolean first = true;
		for (MessageAggregate.AggregateMessagePart part : aggregate.messageParts()) {
			if (first) {
				attachment.setTitle(part.titleWithCount());
				first = false;
			} else {
				writer.append(part.titleWithCount()).append("\n");
			}
		}
		attachment.setText(writer.toString());
		attachment.setColor(levelColor(aggregate.getMaxLevel()));
		payload.addAttachment(attachment);
		client.post(payload);
	}

	@Override
	protected void publishOne(Message message) {
		Payload payload = new Payload();
		addMainAttachment(payload, message);
		String logMessage = "[Slack channel \"" + name + "\"] " + processorService.prettyJson(payload);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout("(debug) " + logMessage);
			return;
		}
		client.post(payload);
	}

	@Override
	protected void publishLimitReached(Rate rateLimit) {
		Payload payload = new Payload();
		payload.setText(limitReachedMessage(rateLimit) +"\nMuting for a while");
		client.post(payload);
	}

	private String findWebhookUrl(String channelName) {
		if ((config.getChannels() == null) || (config.getChannels().get(channelName) == null)) {
			throw new RuntimeError("Slack channel configuration not found: " + channelName);
		}
		SlackConfiguration.ChannelConfig channelConfig = config.getChannels().get(channelName);
		String webhookUrl = channelConfig.getWebhookUrl();
		if (webhookUrl == null) {
			throw new RuntimeError("Slack webhookUrl not defined for channel \"" + channelName + "\"");
		}
		return webhookUrl;
	}

	private void addMainAttachment(Payload payload, Message message) {
		Attachment attachment = new Attachment();
		String title = displayTitle(message);
		attachment.setColor(levelColor(message.getLevel()));
		Object body = message.getBody();
		if (body != null) {
			if (!processorService.getScriptService().bean(body)) {
				attachment.setText(title + "\n" + body.toString());
			} else {
				attachment.setText(title);
				processorService.getScriptService().mapFields(body, (key, value) -> {
					if (value != null) {
						attachment.addField(new Field(key, value.toString(), false));
					}
				});
			}
		}
		attachment.addField(new Field("Level", message.getLevel().name(), true));
		attachment.addField(new Field("Hostname", message.getContext().getHostname(), true));
		if (!empty(attachment)) {
			payload.addAttachment(attachment);
		}
	}

	private String displayTitle(Message message) {
		return levelPrefix(message.getLevel()) + capitalize(message.getType().getDescriptor()) + ": " + message.getTitle();
	}

	private boolean empty(Attachment attachment) {
		return (attachment.getTitle() == null) && (attachment.getText() == null) && attachment.getFields().isEmpty();
	}

	private Color levelColor(Message.Level level) {
		switch (level) {
			case lowest:
			case low:
				return Color.GOOD;
			case medium:
				return Color.WARNING;
			case high:
				return Color.DANGER;
			case emergency:
				return Color.DANGER;
		}
		throw new IllegalStateException("unknown level: " + level.name());
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

}
