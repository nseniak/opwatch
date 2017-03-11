package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory<StatDesc, Stat> {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stat";
	}

	@Override
	public Class<StatDesc> descriptorClass() {
		return StatDesc.class;
	}

	@Override
	public Stat make(Object scriptObject) {
		StatDesc descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), new java.io.File(file));
		return stat;
	}

}
