package com.untrackr.alerter.processor.primitives.transformer.jsgrep;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;
import com.untrackr.alerter.service.ProcessorService;

public class JSGrepFactory extends ActiveProcessorFactory<JSGrepDescriptor, JSGrep> {

	public JSGrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jsgrep";
	}

	@Override
	public Class<JSGrepDescriptor> descriptorClass() {
		return JSGrepDescriptor.class;
	}

	@Override
	public JSGrep make(Object scriptObject) {
		JSGrepDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptPredicate predicate = checkPropertyValue("predicate", descriptor.getPredicate());
		JSGrep jsgrep = new JSGrep(getProcessorService(), descriptor, type(), predicate);
		return jsgrep;
	}

}
