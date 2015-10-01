package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.model.common.JsonUtil;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JS extends Filter {

	private ScriptEngine engine;
	private String value;

	public JS(ProcessorService processorService, IncludePath path, String value) {
		super(processorService, path);
		this.value = value;
		this.engine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	@Override
	public void initialize() {
		// Nothing to do
	}

	@Override
	public void consume(Payload payload) {
		Bindings bindings = engine.createBindings();
		// Copy the input because the js code might do side effects on it
		Object inputCopy = JsonUtil.deepCopy(payload.getJsonObject());
		bindings.put("input", inputCopy);
		Object result;
		try {
			result = engine.eval(value, bindings);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
		if (result != null) {
			outputFiltered(result, payload);
		}
	}

	@Override
	public String identifier() {
		return value;
	}

}
