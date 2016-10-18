package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptPredicate extends JavascriptFunction {

	public JavascriptPredicate(ScriptObjectMirror function) {
		super(function);
	}

	public boolean call(Payload payload, ActiveProcessor processor) {
		try {
			Object result = function.call(function, payload.getScriptObject());
			if (result == Boolean.TRUE) {
				return true;
			} else if (result == Boolean.FALSE) {
				return false;
			} else {
				ProcessorExecutionException error = new ProcessorExecutionException("predicate returned a non-boolean value: " + result.toString(), processor, payload);
				error.setSilent(processor.scriptErrorSignaled(this));
				throw error;
			}
		} catch (NashornException e) {
			RuntimeScriptException exception = new RuntimeScriptException(e, processor, payload);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

}
