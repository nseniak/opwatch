package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory<DfDescriptor, Df> {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "df";
	}

	@Override
	public Class<DfDescriptor> descriptorClass() {
		return DfDescriptor.class;
	}

	@Override
	public Df make(Object scriptObject) {
		DfDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), new java.io.File(file));
		return df;
	}

}
