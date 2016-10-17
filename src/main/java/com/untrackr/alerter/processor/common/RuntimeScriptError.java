package com.untrackr.alerter.processor.common;

/**
 * Exception thrown by Java code implementing Javascript methods to signal an error
 */
public class RuntimeScriptError extends RuntimeException {

	public RuntimeScriptError(String message) {
		super(message);
	}

}
