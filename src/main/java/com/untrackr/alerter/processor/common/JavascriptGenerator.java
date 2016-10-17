package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptGenerator extends JavascriptFunction {

	public JavascriptGenerator(String processorName, ScriptObjectMirror function) {
		super(processorName, function);
	}

	public Object call(ActiveProcessor processor) {
		try {
			return function.call(function);
		} catch (Throwable t) {
			RuntimeProcessorError error = new RuntimeProcessorError(t, processor);
			error.setSilent(processor.scriptErrorSignaled(this));
			throw error;
		}
	}

}
