package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class JSFactory extends ActiveProcessorFactory {

	public JSFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "js";
	}

	@Override
	public JS make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		JSDesc descriptor = convertDescriptor(path, JSDesc.class, jsonDescriptor);
		String value = checkFieldValue(path, jsonDescriptor, "value", descriptor.getValue());
		JS js = new JS(getProcessorService(), path, value);
		initialize(js, descriptor);
		return js;
	}

}
