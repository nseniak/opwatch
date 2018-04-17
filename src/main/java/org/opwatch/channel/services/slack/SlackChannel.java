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

package org.opwatch.channel.services.slack;

import allbegray.slack.SlackClientFactory;
import allbegray.slack.type.Attachment;
import allbegray.slack.type.Color;
import allbegray.slack.type.Field;
import allbegray.slack.type.Payload;
import allbegray.slack.webhook.SlackWebhookClient;
import org.opwatch.channel.common.throttled.MessageAggregate;
import org.opwatch.channel.common.throttled.Rate;
import org.opwatch.channel.common.throttled.RateLimiter;
import org.opwatch.channel.common.throttled.ThrottledChannel;
import org.opwatch.channel.services.slack.SlackConfiguration.ChannelConfig;
import org.opwatch.processor.common.Message;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SlackChannel extends ThrottledChannel<SlackConfiguration> {

	private static final Logger logger = LoggerFactory.getLogger(SlackChannel.class);

	private static final int MAX_ATTACHMENT_TEXT_SIZE = 7900;

	private String name;
	private SlackConfiguration config;
	private SlackMessageService service;
	private String webhookUrl;
	private SlackWebhookClient client;
	private RateLimiter rateLimiter;

	public SlackChannel(String name, SlackConfiguration config, SlackMessageService service, ProcessorService processorService) {
		super(processorService, service);
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		ChannelConfig channelConfig = channelConfiguration(name, config);
		this.webhookUrl = findWebhookUrl(name, channelConfig);
		this.client = SlackClientFactory.createWebhookClient(webhookUrl);
		Rate limit = new Rate((int) TimeUnit.MINUTES.toSeconds(1), channelConfig.getMaxPerMinute());
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
			aggregate.addMessage(displayTitle(message), message);
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
		attachment.setColor(levelColor(aggregate.getMaxLevel(), aggregate.getMessageTypes()));
		payload.addAttachment(attachment);
		client.post(payload);
	}

	@Override
	protected void publishOne(Message message) {
		Payload payload = new Payload();
		addMainAttachment(payload, message);
		String logMessage = logString() + " " + processorService.prettyJson(payload);
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
		payload.setText(limitReachedMessage(rateLimit) + "\nMuting for a while");
		client.post(payload);
	}

	private String findWebhookUrl(String channelName, ChannelConfig channelConfig) {
		String webhookUrl = channelConfig.getWebhookUrl();
		if (webhookUrl == null) {
			throw new RuntimeError("Slack webhookUrl not defined for channel \"" + channelName + "\"");
		}
		return webhookUrl;
	}

	private ChannelConfig channelConfiguration(String channelName, SlackConfiguration config) {
		if ((config.getChannels() == null) || (config.getChannels().get(channelName) == null)) {
			throw new RuntimeError("Slack channel configuration not found: " + channelName);
		}
		return config.getChannels().get(channelName);
	}

	private void addMainAttachment(Payload payload, Message message) {
		Attachment attachment = new Attachment();
		String title = displayTitle(message);
		attachment.setText(title);
		Color color = levelColor(message.getLevel(), new LinkedHashSet<>(Collections.singletonList(message.getType())));
		attachment.setColor(color);
		attachment.addField(new Field("Level", message.getLevel().name(), true));
		attachment.addField(new Field("Hostname", message.getContext().getHostname(), true));
		if (!empty(attachment)) {
			payload.addAttachment(attachment);
		}
		String detailsString = detailsString(message);
		if (detailsString != null) {
			Attachment detailsAttachment = new Attachment();
			detailsAttachment.setColor(color);
			detailsAttachment.setText(mdQuote(detailsString));
			payload.addAttachment(detailsAttachment);
		}
	}

	private boolean empty(Attachment attachment) {
		return (attachment.getTitle() == null) && (attachment.getText() == null) && attachment.getFields().isEmpty();
	}

	private String truncate(String str, int maxLen) {
	    if (str.length() <= maxLen) {
	        return str;
        } else {
	        return str.substring(0, maxLen - 3) + "...";
        }
    }

    private String mdQuote(String str) {
	    return "```" + truncate(str, MAX_ATTACHMENT_TEXT_SIZE - 8) + "```";
    }

	private Color levelColor(Message.Level level, Set<Message.Type> messageTypes) {
		if (messageTypes.contains(Message.Type.alert)
				|| messageTypes.contains(Message.Type.alertOn)
				|| messageTypes.contains(Message.Type.error)) {
			switch (level) {
				case high:
				case emergency:
					return Color.DANGER;
				default:
					return Color.WARNING;
			}
		} else if (messageTypes.contains(Message.Type.alertOff)) {
			return Color.GOOD;
		} else {
			// Default color
			return null;
		}
	}

}
