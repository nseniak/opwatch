package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory<DfConfig, Df> {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "df";
	}

	@Override
	public Class<DfConfig> descriptorClass() {
		return DfConfig.class;
	}

	@Override
	public Df make(Object scriptObject) {
		DfConfig descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), new java.io.File(file));
		return df;
	}

}
