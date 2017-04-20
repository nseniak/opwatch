package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.regex.Pattern;

public abstract class JavascriptFunction extends ConfigPropertyValue {

	protected ScriptObjectMirror function;
	protected ValueLocation valueLocation;
	protected ProcessorService processorService;

	protected JavascriptFunction(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		this.function = function;
		this.valueLocation = valueLocation;
		this.processorService = processorService;
	}

	protected Object invoke(Processor processor, Payload payload) {
		synchronized (processorService) {
			return function.call(function, payload.getValue(), payload);
		}
	}

	protected Object invoke(Processor processor) {
		synchronized (processorService) {
			return function.call(function);
		}
	}

	@Override
	public String toString() {
		return Pattern.compile("\n[\t ]*", Pattern.DOTALL).matcher(function.toString()).replaceAll(" ");
	}

	public ValueLocation getValueLocation() {
		return valueLocation;
	}

	public ScriptObjectMirror getFunction() {
		return function;
	}

}
