package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.config.StringValue;
import com.untrackr.alerter.service.ProcessorService;

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
		AlertGeneratorConfig config = convertProcessorDescriptor(scriptObject);
		String priorityName = checkPropertyValue("priority", config.getPriority());
		Message.Level level;
		try {
			level = Message.Level.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new RuntimeError("bad alert priority: \"" + priorityName + "\"", new FactoryExecutionContext(this));
		}
		StringValue message = checkPropertyValue("message", config.getMessage());
		JavascriptPredicate predicate = config.getTrigger();
		boolean toggle = checkPropertyValue("toggle", config.getToggle());
		String channelName = config.getChannel();
		if ((channelName != null) && (processorService.findChannel(channelName) == null)) {
			throw new RuntimeError("channel not found: \"" + channelName + "\"", new FactoryExecutionContext(this));
		}
		return new AlertGenerator(getProcessorService(), config, name(), message, level, predicate, toggle, channelName);
	}

}
