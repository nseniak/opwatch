package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;

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
		this(cause, null, null, null, -1);
	}

	public RuntimeScriptException(ScriptException cause, Processor processor, Payload payload) {
		this(cause, processor, payload, cause.getFileName(), cause.getLineNumber());
	}

	public RuntimeScriptException(NashornException cause, Processor processor, Payload payload) {
		this(cause, processor, payload, cause.getFileName(), cause.getLineNumber());
	}

	public RuntimeScriptException(Exception cause, Processor processor, Payload payload, String fileName, int lineNumber) {
		super(cause, processor, payload);
		scriptStack = ScriptStack.exceptionStack(cause);
		if (fileName != null) {
			ScriptStack.ScriptStackElement top = scriptStack.top();
			if ((top == null) || !(fileName.equals(top.getFileName()) && (lineNumber == top.getLineNumber()))) {
				scriptStack.addElement(fileName, lineNumber);
			}
		}
	}

	public ScriptStack getScriptStack() {
		return scriptStack;
	}

}
