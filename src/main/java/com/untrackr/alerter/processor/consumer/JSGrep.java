package com.untrackr.alerter.processor.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSGrep extends ConditionalFilter {

	private ScriptEngine engine;
	private String test;

	public JSGrep(ProcessorService processorService, IncludePath path, String test) {
		super(processorService, path);
		this.test = test;
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	@Override
	public boolean conditionValue(Payload input) {
		Bindings bindings = engine.createBindings();
		bindings.put("input", input.getJsonObject());
		Object result;
		try {
			result = engine.eval(test, bindings);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
		if (result == Boolean.TRUE) {
			return true;
		} else if (result == Boolean.FALSE) {
			return false;
		} else {
			String resultString = null;
			try {
				resultString = processorService.getObjectMapper().writeValueAsString(result);
			} catch (JsonProcessingException e) {
				resultString = "<cannot convert to string>";
			}
			throw new RuntimeProcessorError("test returned a non-boolean value: " + resultString, this, input);
		}
	}

}
