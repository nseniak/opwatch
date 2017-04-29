package org.opwatch.processor.config;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptProducer extends JavascriptFunction {

	public JavascriptProducer(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public Object call(Processor processor) {
		return invoke(processor);
	}

}
