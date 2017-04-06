package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.config.StringValue;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGenerator extends Consumer<AlertGeneratorConfig> {

	private Message.Level level;
	private StringValue message;
	private JavascriptPredicate predicate;
	private boolean toggle;
	private boolean toggleUp;
	private String channelName;

	public AlertGenerator(ProcessorService processorService, AlertGeneratorConfig descriptor, String name, StringValue message,
												Message.Level level, JavascriptPredicate predicate, boolean toggle, String channelName) {
		super(processorService, descriptor, name);
		this.level = level;
		this.message = message;
		this.predicate = predicate;
		this.toggle = toggle;
		this.channelName = channelName;
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		boolean alert = (predicate == null) || predicate.call(payload, this);
		Channel channel;
		if (channelName == null) {
			channel = processorService.alertChannel();
		} else {
			channel = processorService.findChannel(channelName);
			if (channel == null) {
				throw new RuntimeError("channel not found: \"" + channelName + "\"", new ProcessorVoidExecutionContext(this));
			}
		}
		if (!toggle) {
			if (alert) {
				processorService.publish(channel, makeAlerterMessage(Message.Type.alert, payload));
			}
		} else {
			if (!toggleUp && alert) {
				processorService.publish(channel, makeAlerterMessage(Message.Type.alertStart, payload));
			} else if (toggleUp && !alert) {
				processorService.publish(channel, makeAlerterMessage(Message.Type.alertEnd, payload));
			}
			toggleUp = alert;
		}
	}

	private Message makeAlerterMessage(Message.Type type, Payload<?> payload) {
		ExecutionContext context = new ProcessorPayloadExecutionContext(this, payload);
		MessageScope scope = context.makeMessageScope(processorService);
		String title = message.value(this, payload);
		MessageData messageData = new MessageData();
		context.addContextData(messageData, processorService);
		return new Message(type, level, title, null, scope, messageData);
	}

}
