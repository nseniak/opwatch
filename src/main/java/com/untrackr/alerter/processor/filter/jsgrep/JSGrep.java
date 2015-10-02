package com.untrackr.alerter.processor.filter.jsgrep;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.untrackr.alerter.model.common.JsonUtil;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.*;

public class JSGrep extends ConditionalFilter {

	private String source;
	private CompiledScript test;
	private Bindings bindings;
	private boolean nonBooleanValueErrorSignaled = false;

	public JSGrep(ProcessorService processorService, IncludePath path, String source, CompiledScript test) {
		super(processorService, path);
		this.source = source;
		this.test = test;
		this.bindings = processorService.getNashorn().createBindings();
	}

	@Override
	public boolean conditionValue(Payload input) {
		// Copy the input because the js code might do side effects on it
		Object inputCopy = JsonUtil.deepCopy(input.getJsonObject());
		bindings.put("input", inputCopy);
		Object result;
		try {
			result = test.eval(bindings);
		} catch (ScriptException e) {
			throw new RuntimeProcessorError(e, this, input);
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
		return source;
	}

}
