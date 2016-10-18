package com.untrackr.alerter.processor.common;

import javax.script.ScriptException;

/**
 * Error signaled by Java code implementing JavaScript functions
 */
public class RuntimeScriptException extends AlerterException {

	private ScriptStack scriptStack;

	public RuntimeScriptException(String message) {
		super(message);
		scriptStack = ScriptStack.currentStack();
	}

	public RuntimeScriptException(ScriptException cause) {
		super(cause);
		scriptStack = ScriptStack.exceptionStack(cause);
	}

	public ScriptStack getScriptStack() {
		return scriptStack;
	}

}
