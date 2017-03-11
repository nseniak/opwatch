package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory<DfDesc, Df> {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "df";
	}

	@Override
	public Class<DfDesc> descriptorClass() {
		return DfDesc.class;
	}

	@Override
	public Df make(Object scriptObject) {
		DfDesc descriptor = convertProcessorDescriptor(scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), new java.io.File(file));
		return df;
	}

}
