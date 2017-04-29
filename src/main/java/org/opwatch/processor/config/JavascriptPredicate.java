package org.opwatch.processor.config;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;

public class JavascriptPredicate extends JavascriptFunction {

	public JavascriptPredicate(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public boolean call(Payload payload, Processor processor) {
		Object result = invoke(processor, payload);
		ScriptService scriptService = processor.getProcessorService().getScriptService();
		return (boolean) scriptService.convertScriptValue(valueLocation, Boolean.class, result,
				(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
	}

}
