package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptProducer extends JavascriptFunction {

	public JavascriptProducer(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public Object call(Processor processor) {
		return invoke(processor);
	}

}
