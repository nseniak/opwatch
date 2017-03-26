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
		RepeatConfig descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptProducer producer = checkPropertyValue("producer", descriptor.getProducer());
		return new Repeat(getProcessorService(), descriptor, name(), executor, producer);
	}

}
