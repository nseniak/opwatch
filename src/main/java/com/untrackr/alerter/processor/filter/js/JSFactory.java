package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class JSFactory extends ActiveProcessorFactory {

	public JSFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "js";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor scriptDescriptor = scriptDescriptor(object);
		JSDesc descriptor = convertScriptDescriptor(JSDesc.class, scriptDescriptor);
		JavascriptTransformer transformer = checkFieldValue(scriptDescriptor, "transformer", descriptor.getTransformer());
		JS js = new JS(getProcessorService(), ScriptStack.currentStack(), transformer);
		initialize(js, descriptor);
		return js;
	}

}
