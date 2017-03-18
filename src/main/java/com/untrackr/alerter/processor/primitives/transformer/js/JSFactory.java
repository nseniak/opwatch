package com.untrackr.alerter.processor.primitives.transformer.js;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class JSFactory extends ActiveProcessorFactory<JSDescriptor, JS> {

	public JSFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "js";
	}

	@Override
	public Class<JSDescriptor> descriptorClass() {
		return JSDescriptor.class;
	}

	@Override
	public JS make(Object scriptObject) {
		JSDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptTransformer transformer = checkPropertyValue("transformer", descriptor.getTransformer());
		JS js = new JS(getProcessorService(), descriptor, type(), transformer);
		return js;
	}

}
