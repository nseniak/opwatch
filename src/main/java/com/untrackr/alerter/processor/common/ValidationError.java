package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.common.JsonDescriptor;

public class ValidationError extends RuntimeScriptError {

	JsonDescriptor descriptor;

	public ValidationError(String message, JsonDescriptor descriptor) {
		super(message);
		this.descriptor = descriptor;
	}

}
