package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.model.common.JsonDescriptor;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		DfDesc descriptor = convertScriptDescriptor(DfDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(jsonDescriptor, "file", checkFieldValue(jsonDescriptor, "file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor), new java.io.File(file));
		initialize(df, descriptor);
		return df;
	}

}
