package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.CompiledScript;

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
		String testSource = checkFieldValue(path, jsonDescriptor, "test", descriptor.getTest());
		CompiledScript testScript = compileScript(path, jsonDescriptor, "test", testSource);
		JSGrep jsgrep = new JSGrep(getProcessorService(), path, testSource, testScript);
		initialize(jsgrep, descriptor);
		return jsgrep;
	}

}
