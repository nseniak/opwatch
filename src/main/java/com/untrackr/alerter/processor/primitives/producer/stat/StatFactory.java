package com.untrackr.alerter.processor.primitives.producer.stat;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory<StatConfig, Stat> {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stat";
	}

	@Override
	public Class<StatConfig> configurationClass() {
		return StatConfig.class;
	}

	@Override
	public Class<Stat> processorClass() {
		return Stat.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Stat make(Object scriptObject) {
		StatConfig descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		return new Stat(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), new java.io.File(file));
	}

}
