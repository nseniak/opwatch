package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.regex.Pattern;

public abstract class JavascriptFunction {

	protected ScriptObjectMirror function;
	protected ValueLocation valueLocation;

	protected JavascriptFunction(ScriptObjectMirror function, ValueLocation valueLocation) {
		this.function = function;
		this.valueLocation = valueLocation;
	}

	protected Object invoke(Processor processor, Payload payload) {
		try {
			return function.call(function, payload.getValue(), payload);
		} catch (NashornException e) {
			ExceptionContext context = ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation, e), payload);
			AlerterException exception = new AlerterException(e, context);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

	protected Object invoke(Processor processor) {
		try {
			return function.call(function);
		} catch (NashornException e) {
			ExceptionContext context = ExceptionContext.makeProcessorNoPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation, e));
			AlerterException exception = new AlerterException(e, context);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

	@Override
	public String toString() {
		return Pattern.compile("\n[\t ]*", Pattern.DOTALL).matcher(function.toString()).replaceAll(" ");
	}

	public ValueLocation getValueLocation() {
		return valueLocation;
	}

}
