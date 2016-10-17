package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrepFactory extends ActiveProcessorFactory {

	public JSGrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jsgrep";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor scriptDescriptor = scriptDescriptor(object);
		JSGrepDesc descriptor = convertScriptDescriptor(JSGrepDesc.class, scriptDescriptor);
		JavascriptPredicate predicate = checkFieldValue(scriptDescriptor, "predicate", descriptor.getPredicate());
		JSGrep jsgrep = new JSGrep(getProcessorService(), ScriptStack.currentStack(), predicate);
		initialize(jsgrep, descriptor);
		return jsgrep;
	}

}
