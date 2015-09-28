package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.DfDesc;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Df make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		DfDesc descriptor = convertDescriptor(path, DfDesc.class, jsonObject);
		String file = checkVariableSubstitution(path, jsonObject, "file", fieldValue(path, jsonObject, "file", descriptor.getFile()));
		Df df = new Df(getProcessorService(), path, makeScheduledExecutor(descriptor), new java.io.File(file));
		initialize(df, descriptor);
		return df;
	}

}
