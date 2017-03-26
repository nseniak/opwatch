package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.processor.payload.Payload;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptFilter extends JavascriptFunction {

	public JavascriptFilter(ScriptObjectMirror function, ValueLocation valueLocation) {
		super(function, valueLocation);
	}

	public Object call(Payload payload, Processor processor) {
		return invoke(processor, payload);
	}

}