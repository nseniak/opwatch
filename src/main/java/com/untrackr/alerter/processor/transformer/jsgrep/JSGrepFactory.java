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
	public JSGrep make(Object scriptObject) {
		JSGrepDesc descriptor = convertProcessorArgument(JSGrepDesc.class, scriptObject);
		JavascriptPredicate predicate = checkPropertyValue("predicate", descriptor.getPredicate());
		JSGrep jsgrep = new JSGrep(getProcessorService(), displayName(descriptor), predicate);
		return jsgrep;
	}

}
