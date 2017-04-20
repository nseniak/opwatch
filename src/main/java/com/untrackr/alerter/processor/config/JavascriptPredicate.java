package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import com.untrackr.alerter.service.ScriptService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptPredicate extends JavascriptFunction {

	public JavascriptPredicate(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public boolean call(Payload payload, Processor processor) {
		Object result = invoke(processor, payload);
		ScriptService scriptService = processor.getProcessorService().getScriptService();
		return (boolean) scriptService.convertScriptValue(valueLocation, Boolean.class, result,
				(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
	}

}
