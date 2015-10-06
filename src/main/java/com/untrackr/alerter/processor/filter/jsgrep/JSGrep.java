package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;

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
		Object result = runScript(test, bindings, input);
		if (result == Boolean.TRUE) {
			return true;
		} else if (result == Boolean.FALSE) {
			return false;
		} else {
			if (nonBooleanValueErrorSignaled) {
				return false;
			} else {
				nonBooleanValueErrorSignaled = true;
				throw new RuntimeProcessorError("test returned a non-boolean value: " + processorService.valueAsString(result), this, input);
			}
		}
	}

	@Override
	public String identifier() {
		return source;
	}

}
