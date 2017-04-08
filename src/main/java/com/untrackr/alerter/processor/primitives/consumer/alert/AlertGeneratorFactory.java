package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.ConstantOrFilter;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
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
		AlertGeneratorConfig config = convertProcessorConfig(scriptObject);
		String priorityName = checkPropertyValue("priority", config.getLevel());
		Message.Level level;
		try {
			level = Message.Level.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new RuntimeError("bad alert priority: \"" + priorityName + "\"", new FactoryExecutionScope(this), e);
		}
		String title = checkPropertyValue("title", config.getTitle());
		JavascriptPredicate trigger = config.getTrigger();
		boolean toggle = checkPropertyValue("toggle", config.getToggle());
		String channelName = config.getChannel();
		if ((channelName != null) && (processorService.findChannel(channelName) == null)) {
			throw new RuntimeError("channel not found: \"" + channelName + "\"", new FactoryExecutionScope(this));
		}
		ConstantOrFilter<Object> body = config.getBody();
		return new AlertGenerator(getProcessorService(), config, name(), title, body, level, trigger, toggle, channelName);
	}

}
