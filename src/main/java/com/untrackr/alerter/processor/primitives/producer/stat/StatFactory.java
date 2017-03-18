package com.untrackr.alerter.processor.primitives.producer.stat;

import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory<StatDescriptor, Stat> {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stat";
	}

	@Override
	public Class<StatDescriptor> descriptorClass() {
		return StatDescriptor.class;
	}

	@Override
	public Stat make(Object scriptObject) {
		StatDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), new java.io.File(file));
		return stat;
	}

}
