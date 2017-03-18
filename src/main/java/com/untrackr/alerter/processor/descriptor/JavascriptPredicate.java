package com.untrackr.alerter.processor.descriptor;

import com.untrackr.alerter.processor.common.CallbackErrorLocation;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ScriptService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptPredicate extends JavascriptFunction {

	public JavascriptPredicate(ScriptObjectMirror function, ValueLocation valueLocation) {
		super(function, valueLocation);
	}

	public boolean call(Payload payload, Processor processor) {
		Object result = invoke(processor, payload);
		ScriptService scriptService = processor.getProcessorService().getScriptService();
		return (boolean) scriptService.convertScriptValue(valueLocation, Boolean.class, result,
				() -> ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation), payload));
	}

}
