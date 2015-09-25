package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.model.descriptor.JSGrepDesc;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrepFactory extends Factory {

	public JSGrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jsgrep";
	}

	@Override
	public JSGrep make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		JSGrepDesc descriptor = convertDescriptor(path, JSGrepDesc.class, jsonObject);
		String test = fieldValue(path, jsonObject, "test", descriptor.getTest());
		return new JSGrep(getProcessorService(), path, test);
	}

}
