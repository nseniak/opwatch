package com.untrackr.alerter.processor.transformer.js;

import com.untrackr.alerter.processor.common.*;
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
	public JS make(Object scriptObject) {
		JSDesc descriptor = convertProcessorDescriptor(JSDesc.class, scriptObject);
		JavascriptTransformer transformer = checkPropertyValue("transformer", descriptor.getTransformer());
		JS js = new JS(getProcessorService(), descriptor, type(), transformer);
		return js;
	}

}
