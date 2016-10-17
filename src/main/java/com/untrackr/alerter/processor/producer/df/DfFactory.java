package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object scriptObject) throws ValidationError {
		DfDesc descriptor = convertProcessorArgument(DfDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkFieldValue("file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(descriptor), new java.io.File(file));
		initialize(df, descriptor);
		return df;
	}

}
