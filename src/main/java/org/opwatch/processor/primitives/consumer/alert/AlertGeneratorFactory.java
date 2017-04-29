package org.opwatch.processor.primitives.consumer.alert;

import org.opwatch.processor.common.*;
import org.opwatch.processor.config.ConstantOrFilter;
import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.service.ProcessorService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AlertGeneratorFactory extends ActiveProcessorFactory<AlertGeneratorConfig, AlertGenerator> {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alert";
	}

	@Override
	public Class<AlertGeneratorConfig> configurationClass() {
		return AlertGeneratorConfig.class;
	}

	@Override
	public Class<AlertGenerator> processorClass() {
		return AlertGenerator.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	@Override
	public AlertGenerator make(Object scriptObject) {
		AlertGeneratorConfig config = convertProcessorConfig(scriptObject);
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
		ConstantOrFilter<Object> body = config.getBody();
		return new AlertGenerator(getProcessorService(), config, name(), title, body, level, trigger, toggle, channelName);
	}

}
