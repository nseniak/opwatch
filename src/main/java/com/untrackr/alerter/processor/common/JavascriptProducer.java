package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptProducer extends JavascriptFunction {

	public JavascriptProducer(ScriptObjectMirror function) {
		super(function);
	}

	public Object call(ActiveProcessor processor) {
		try {
			return function.call(function);
		} catch (Throwable t) {
			ProcessorExecutionException error = new ProcessorExecutionException(t, processor);
			error.setSilent(processor.scriptErrorSignaled(this));
			throw error;
		}
	}

}
