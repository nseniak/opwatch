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

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.*;
import org.opwatch.processor.config.ValueOrFilter;
import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.service.ProcessorService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AlertGeneratorFactory extends ActiveProcessorFactory<AlertProducerConfig, AlertProducer> {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alert";
	}

	@Override
	public Class<AlertProducerConfig> configurationClass() {
		return AlertProducerConfig.class;
	}

	@Override
	public Class<AlertProducer> processorClass() {
		return AlertProducer.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.consumer;
	}

	@Override
	public AlertProducer make(Object scriptObject) {
		AlertProducerConfig config = convertProcessorConfig(scriptObject);
		String priorityName = checkPropertyValue("priority", config.getLevel());
		Message.Level level;
		try {
			level = Message.Level.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			List<String> allowed = Arrays.asList(Message.Level.values()).stream().map(Message.Level::name).collect(Collectors.toList());
			String allowedString = String.join(", ", allowed);
			throw new RuntimeError("incorrect alert level: \"" + priorityName + "\"; must be one of: " + allowedString,
					new FactoryExecutionScope(this), e);
		}
		String title = checkPropertyValue("title", config.getTitle());
		JavascriptPredicate trigger = config.getTrigger();
		boolean toggle = checkPropertyValue("toggle", config.getToggle());
		String channelName = config.getChannel();
		if ((channelName != null) && (processorService.getMessagingService().findChannel(channelName) == null)) {
			throw new RuntimeError("channel not found: \"" + channelName + "\"", new FactoryExecutionScope(this));
		}
		ValueOrFilter<Object> details = config.getDetails();
		return new AlertProducer(getProcessorService(), config, name(), title, details, level, trigger, toggle, channelName);
	}

}
