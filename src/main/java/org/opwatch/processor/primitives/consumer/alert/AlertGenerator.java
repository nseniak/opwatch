package org.opwatch.processor.primitives.consumer.alert;

import org.opwatch.channel.common.Channel;
import org.opwatch.processor.common.*;
import org.opwatch.processor.config.ConstantOrFilter;
import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public class AlertGenerator extends ThreadedConsumer<AlertGeneratorConfig> {

	private Message.Level level;
	private String title;
	private ConstantOrFilter<Object> body;
	private JavascriptPredicate trigger;
	private boolean toggle;
	private boolean toggleUp;
	private String channelName;

	public AlertGenerator(ProcessorService processorService, AlertGeneratorConfig configuration, String name, String title,
												ConstantOrFilter<Object> body, Message.Level level, JavascriptPredicate trigger, boolean toggle, String channelName) {
		super(processorService, configuration, name);
		this.level = level;
		this.title = title;
		this.body = body;
		this.trigger = trigger;
		this.toggle = toggle;
		this.channelName = channelName;
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		boolean alert = (trigger == null) || trigger.call(payload, this);
		Channel channel;
		if (channelName == null) {
			channel = processorService.getMessagingService().applicationChannel();
		} else {
			channel = processorService.getMessagingService().findChannel(channelName);
			if (channel == null) {
				throw new RuntimeError("channel not found: \"" + channelName + "\"", new ProcessorVoidExecutionScope(this));
			}
		}
		if (!toggle) {
			if (alert) {
				processorService.publish(channel, makeAlertMessage(Message.Type.alert, payload));
			}
		} else {
			if (!toggleUp && alert) {
				processorService.publish(channel, makeAlertMessage(Message.Type.alertStart, payload));
			} else if (toggleUp && !alert) {
				processorService.publish(channel, makeAlertMessage(Message.Type.alertEnd, payload));
			}
			toggleUp = alert;
		}
	}

	private Message makeAlertMessage(Message.Type type, Payload<?> payload) {
		ExecutionScope scope = new ProcessorPayloadExecutionScope(this, payload);
		MessageContext context = scope.makeContext(processorService, constructionStack);
		Object bodyValue = null;
		if (body != null) {
			bodyValue = body.value(this, payload, Object.class);
		} else {
			bodyValue = payload.getValue();
		}
		return Message.makeNew(type, level, title, bodyValue, context);
	}

}
