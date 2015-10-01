package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.model.common.JsonUtil;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.*;

public class JS extends Filter {

	private String source;
	private CompiledScript value;

	public JS(ProcessorService processorService, IncludePath path, String source, CompiledScript value) {
		super(processorService, path);
		this.source = source;
		this.value = value;
	}

	@Override
	public void consume(Payload payload) {
		Bindings bindings = processorService.getNashorn().createBindings();
		// Copy the input because the js code might do side effects on it
		Object inputCopy = JsonUtil.deepCopy(payload.getJsonObject());
		bindings.put("input", inputCopy);
		Object result;
		try {
			result = value.eval(bindings);
		} catch (ScriptException e) {
			throw new RuntimeProcessorError(e, this, payload);
		}
		if (result != null) {
			outputFiltered(result, payload);
		}
	}

	@Override
	public String identifier() {
		return source;
	}

}
