package com.untrackr.alerter.processor.transformer.js;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class JSFactory extends ActiveProcessorFactory<JSDesc, JS> {

	public JSFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "js";
	}

	@Override
	public Class<JSDesc> descriptorClass() {
		return JSDesc.class;
	}

	@Override
	public JS make(Object scriptObject) {
		JSDesc descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptTransformer transformer = checkPropertyValue("transformer", descriptor.getTransformer());
		JS js = new JS(getProcessorService(), descriptor, type(), transformer);
		return js;
	}

}
