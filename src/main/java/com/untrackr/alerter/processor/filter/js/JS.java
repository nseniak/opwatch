package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;

public class JS extends Filter {

	private String source;
	private CompiledScript value;
	private Bindings bindings;

	public JS(ProcessorService processorService, IncludePath path, String source, CompiledScript value) {
		super(processorService, path);
		this.source = source;
		this.value = value;
		this.bindings = processorService.getNashorn().createBindings();
	}

	@Override
	public void consume(Payload payload) {
		Object result = runScript(value, bindings, payload);
		if (result != null) {
			outputFiltered(result, payload);
		}
	}

	@Override
	public String identifier() {
		return source;
	}

}
