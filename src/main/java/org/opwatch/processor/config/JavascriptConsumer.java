package org.opwatch.processor.config;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public class JavascriptConsumer extends JavascriptFunction {

	public JavascriptConsumer(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public void call(Payload payload, Processor processor) {
		invoke(processor, payload);
	}

}
