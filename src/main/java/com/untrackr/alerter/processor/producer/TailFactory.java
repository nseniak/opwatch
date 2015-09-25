package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.model.descriptor.TailDesc;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.io.File;

public class TailFactory extends Factory {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "tail";
	}

	@Override
	public Tail make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		TailDesc descriptor = convertDescriptor(path, TailDesc.class, jsonObject);
		String file = checkVariableSubstitution(path, jsonObject, "file", fieldValue(path, jsonObject, "file", descriptor.getFile()));
		return new Tail(getProcessorService(), path, new File(file));
	}

}
