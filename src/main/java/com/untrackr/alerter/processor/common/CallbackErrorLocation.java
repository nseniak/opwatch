package com.untrackr.alerter.processor.common;

import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;

public class CallbackErrorLocation {

	/**
	 * Location of the callback
	 */
	private ValueLocation callbackLocation;
	/**
	 * Stack at the moment where the error occurs in the callback
	 */
	private ScriptStack scriptStack;

	public CallbackErrorLocation(ValueLocation callbackLocation, ScriptException exception) {
		this.callbackLocation = callbackLocation;
		this.scriptStack = ScriptStack.exceptionStack(exception);
	}

	public CallbackErrorLocation(ValueLocation callbackLocation, NashornException exception) {
		this.callbackLocation = callbackLocation;
		this.scriptStack = ScriptStack.exceptionStack(exception);
	}

	public CallbackErrorLocation(ValueLocation callbackLocation) {
		this.callbackLocation = callbackLocation;
		this.scriptStack = ScriptStack.currentStack();
	}

	public String descriptor() {
		StringBuilder stringBuilder = new StringBuilder();
		String location = callbackLocation.describeAsLocation();
		if (location != null) {
			stringBuilder.append("[").append(location).append("] ");
		}
		if (!scriptStack.empty()) {
			stringBuilder.append(scriptStack.asString());
		}
		if (stringBuilder.length() == 0) {
			return null;
		} else {
			return stringBuilder.toString();
		}
	}

	public ValueLocation getCallbackLocation() {
		return callbackLocation;
	}

	public ScriptStack getScriptStack() {
		return scriptStack;
	}

}
