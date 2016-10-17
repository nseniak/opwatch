package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.regex.Pattern;

public abstract class JavascriptFunction {

	protected ScriptObjectMirror function;

	protected JavascriptFunction(String processorName, ScriptObjectMirror function) {
		if (!function.isFunction()) {
			throw new ValidationError(processorName + ": not a function", null);
		}
		this.function = function;
	}

	@Override
	public String toString() {
		return Pattern.compile("\n[\t ]*", Pattern.DOTALL).matcher(function.toString()).replaceAll(" ");
	}

}
