package com.untrackr.alerter.processor.producer.df;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "df";
	}

	@Override
	public Df make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		DfDesc descriptor = convertDescriptor(path, DfDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(path, jsonDescriptor, "file", checkFieldValue(path, jsonDescriptor, "file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), path, makeScheduledExecutor(path, jsonDescriptor, descriptor), new java.io.File(file));
		initialize(df, descriptor);
		return df;
	}

}
