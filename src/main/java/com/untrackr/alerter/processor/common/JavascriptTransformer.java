package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptTransformer extends JavascriptFunction {

	public JavascriptTransformer(String processorName, ScriptObjectMirror function) {
		super(processorName, function);
	}

	public Object call(Payload payload, ActiveProcessor processor) {
		try {
			return function.call(function, payload.getScriptObject());
		} catch (Throwable t) {
			RuntimeProcessorError error = new RuntimeProcessorError(t, processor, payload);
			error.setSilent(processor.scriptErrorSignaled(this));
			throw error;
		}
	}

}
