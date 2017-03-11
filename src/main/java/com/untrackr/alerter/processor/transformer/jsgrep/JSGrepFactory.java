package com.untrackr.alerter.processor.transformer.jsgrep;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrepFactory extends ActiveProcessorFactory<JSGrepDesc, JSGrep> {

	public JSGrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jsgrep";
	}

	@Override
	public Class<JSGrepDesc> descriptorClass() {
		return JSGrepDesc.class;
	}

	@Override
	public JSGrep make(Object scriptObject) {
		JSGrepDesc descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptPredicate predicate = checkPropertyValue("predicate", descriptor.getPredicate());
		JSGrep jsgrep = new JSGrep(getProcessorService(), descriptor, type(), predicate);
		return jsgrep;
	}

}
