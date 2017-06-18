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

package org.opwatch.processor.primitives.consumer.alert;

import org.opwatch.channel.common.Channel;
import org.opwatch.processor.common.*;
import org.opwatch.processor.config.ValueOrFilter;
import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public class AlertProducer extends ThreadedConsumer<AlertProducerConfig> {

	private Message.Level level;
	private String title;
	private ValueOrFilter<Object> details;
	private JavascriptPredicate trigger;
	private boolean toggle;
	private boolean toggleOn;
	private String channelName;

	public AlertProducer(ProcessorService processorService, AlertProducerConfig configuration, String name, String title,
											 ValueOrFilter<Object> details, Message.Level level, JavascriptPredicate trigger, boolean toggle, String channelName) {
		super(processorService, configuration, name);
		this.level = level;
		this.title = title;
		this.details = details;
		this.trigger = trigger;
		this.toggle = toggle;
		this.channelName = channelName;
	}

	@Override
	public void consumeInOwnThread(Payload payload) {
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
			if (!toggleOn && alert) {
				processorService.publish(channel, makeAlertMessage(Message.Type.alertOn, payload));
			} else if (toggleOn && !alert) {
				processorService.publish(channel, makeAlertMessage(Message.Type.alertOff, payload));
			}
			toggleOn = alert;
		}
	}

	private Message makeAlertMessage(Message.Type type, Payload payload) {
		ExecutionScope scope = new ProcessorPayloadExecutionScope(this, payload);
		MessageContext context = scope.makeContext(processorService, constructionStack);
		Object detailsValue = null;
		if (details != null) {
			detailsValue = details.value(this, payload, Object.class);
		} else {
			detailsValue = payload.getValue();
		}
		return Message.makeNew(type, level, title, detailsValue, context);
	}

}
