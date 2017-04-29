package org.opwatch.processor.config;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class JavascriptFilter extends JavascriptFunction {

	public JavascriptFilter(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public Object call(Payload payload, Processor processor) {
		return invoke(processor, payload);
	}

}
