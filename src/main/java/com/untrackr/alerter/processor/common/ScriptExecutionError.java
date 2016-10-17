package com.untrackr.alerter.processor.common;

public class ScriptExecutionError extends RuntimeException {

	private ScriptStack scriptStack;

	public ScriptExecutionError(String message, ScriptStack scriptStack) {
		super(message);
		this.scriptStack = scriptStack;
	}

	public ScriptExecutionError(String message, Throwable cause, ScriptStack scriptStack) {
		super(message, cause);
		this.scriptStack = scriptStack;
	}

	public ScriptStack getScriptStack() {
		return scriptStack;
	}

}
