package com.untrackr.alerter.channel.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Color;
import allbegray.slack.type.Field;
import allbegray.slack.type.Payload;
import allbegray.slack.webhook.SlackWebhookClient;
import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.processor.common.Message;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.util.StringUtils.capitalize;

public class SlackChannel implements Channel {

	private static final Logger logger = LoggerFactory.getLogger(SlackChannel.class);

	private String name;
	private SlackConfiguration config;
	private SlackMessageService service;
	private ProcessorService processorService;
	private String webhookUrl;

	public SlackChannel(String name, SlackConfiguration config, SlackMessageService service, ProcessorService processorService) {
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		this.webhookUrl = findWebhookUrl(name);
	}

	@Override
	public String serviceName() {
		return service.serviceName();
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

	@Override
	public String name() {
		return name;
	}

	@Override
	public void publish(Message message) {
		SlackWebhookClient client = SlackClientFactory.createWebhookClient(webhookUrl);
		Payload payload = new Payload();
		addMainAttachment(payload, message);
		String logMessage = "Message to Slack[" + name + "] " + processorService.prettyJson(payload);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout("(debug) " + logMessage);
			return;
		}
		client.post(payload);
	}

	private void addMainAttachment(Payload payload, Message message) {
		Attachment attachment = new Attachment();
		attachment.setColor(levelColor(message.getLevel()));
		attachment.setTitle(levelPrefix(message.getLevel()) + capitalize(message.getType().getDescriptor()) + ": " + message.getTitle());
		Object body = message.getBody();
		if (body != null) {
			if (!processorService.getScriptService().bean(body)) {
				attachment.setText(body.toString());
			} else {
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

	private boolean empty(Attachment attachment) {
		return (attachment.getTitle() == null) && (attachment.getText() == null) && attachment.getFields().isEmpty();
	}

	private String alertTitle(Message message) {
		return levelPrefix(message.getLevel()) + message.getType().getDescriptor() + ": " + message.getTitle();
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

	private String truncate(String str, int length) {
		if (str.length() <= length) {
			return str;
		} else {
			return str.substring(0, Math.max(0, length - 3)) + "...";
		}
	}

}
