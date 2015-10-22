package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.ConditionalFilter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;

public class JSGrep extends ConditionalFilter {

	private String source;
	private CompiledScript test;
	private Bindings bindings;

	public JSGrep(ProcessorService processorService, IncludePath path, String source, CompiledScript test) {
		super(processorService, path);
		this.source = source;
		this.test = test;
		this.bindings = processorService.getNashorn().createBindings();
	}

	@Override
	public boolean conditionValue(Payload input) {
		return scriptBooleanValue(test, bindings, input);
	}

	@Override
	public String identifier() {
		return source;
	}

}
