package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrepFactory extends ActiveProcessorFactory {

	public JSGrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jsgrep";
	}

	@Override
	public JSGrep make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		JSGrepDesc descriptor = convertDescriptor(path, JSGrepDesc.class, jsonDescriptor);
		String test = checkFieldValue(path, jsonDescriptor, "test", descriptor.getTest());
		JSGrep jsgrep = new JSGrep(getProcessorService(), path, test);
		initialize(jsgrep, descriptor);
		return jsgrep;
	}

}
