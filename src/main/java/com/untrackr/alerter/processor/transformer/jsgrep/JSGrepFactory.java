package com.untrackr.alerter.processor.transformer.jsgrep;

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
	public Processor make(Object scriptObject) throws ValidationError {
		JSGrepDesc descriptor = convertProcessorArgument(JSGrepDesc.class, scriptObject);
		JavascriptPredicate predicate = checkFieldValue("predicate", descriptor.getPredicate());
		JSGrep jsgrep = new JSGrep(getProcessorService(), ScriptStack.currentStack(), predicate);
		initialize(jsgrep, descriptor);
		return jsgrep;
	}

}
