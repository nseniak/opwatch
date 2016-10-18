package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptTransformer extends JavascriptFunction {

	public JavascriptTransformer(ScriptObjectMirror function) {
		super(function);
	}

	public Object call(Payload payload, ActiveProcessor processor) {
		try {
			return function.call(function, payload.getScriptObject());
		} catch (NashornException e) {
			RuntimeScriptException exception = new RuntimeScriptException(e, processor, payload);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

}
