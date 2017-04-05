package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.config.JavascriptProducer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class RepeatFactory extends ScheduledExecutorFactory<RepeatConfig, Repeat> {

	public RepeatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "repeat";
	}

	@Override
	public Class<RepeatConfig> configurationClass() {
		return RepeatConfig.class;
	}

	@Override
	public Class<Repeat> processorClass() {
		return Repeat.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Repeat make(Object scriptObject) {
		RepeatConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config);
		JavascriptProducer producer = checkPropertyValue("producer", config.getProducer());
		return new Repeat(getProcessorService(), config, name(), executor, producer);
	}

}
