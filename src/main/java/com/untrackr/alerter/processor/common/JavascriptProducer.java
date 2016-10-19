package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptProducer extends JavascriptFunction {

	public JavascriptProducer(ScriptObjectMirror function, ValueLocation valueLocation) {
		super(function, valueLocation);
	}

	public Object call(Processor processor) {
		return invoke(processor);
	}

}
