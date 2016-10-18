package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptProducer extends JavascriptFunction {

	public JavascriptProducer(ScriptObjectMirror function) {
		super(function);
	}

	public Object call(ActiveProcessor processor) {
		try {
			return function.call(function);
		} catch (NashornException e) {
			RuntimeScriptException exception = new RuntimeScriptException(e, processor, null);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

}
