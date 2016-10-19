package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "df";
	}

	@Override
	public Processor make(Object scriptObject) {
		DfDesc descriptor = convertProcessorArgument(DfDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), displayName(descriptor), makeScheduledExecutor(descriptor), new java.io.File(file));
		return df;
	}

}
