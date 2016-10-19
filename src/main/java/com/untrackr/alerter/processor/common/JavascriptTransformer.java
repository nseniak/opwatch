package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptTransformer extends JavascriptFunction {

	public JavascriptTransformer(ScriptObjectMirror function, ValueLocation valueLocation) {
		super(function, valueLocation);
	}

	public Object call(Payload payload, Processor processor) {
		return invoke(processor, payload);
	}

}
