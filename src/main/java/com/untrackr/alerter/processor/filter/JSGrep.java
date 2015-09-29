package com.untrackr.alerter.processor.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSGrep extends ConditionalFilter {

	private ScriptEngine engine;
	private String test;
	private boolean nonBooleanValueErrorSignaled = false;

	public JSGrep(ProcessorService processorService, IncludePath path, String test) {
		super(processorService, path);
		this.test = test;
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	@Override
	public boolean conditionValue(Payload input) {
		Bindings bindings = engine.createBindings();
		// Copy the input because the js code might do side effects on it
		JsonObject inputCopy = JsonObject.deepCopy(input.getJsonObject());
		bindings.put("input", inputCopy);
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
			if (nonBooleanValueErrorSignaled) {
				return false;
			} else {
				nonBooleanValueErrorSignaled = true;
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

	@Override
	public String identifier() {
		return test;
	}

}
