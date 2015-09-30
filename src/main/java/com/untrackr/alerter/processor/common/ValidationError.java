package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.common.JsonDescriptor;

public class ValidationError extends RuntimeException {

	private IncludePath path;
	private JsonDescriptor descriptor;

	public ValidationError(String message, IncludePath path) {
		super(message);
		this.path = path;
	}

	public ValidationError(String message, IncludePath path, JsonDescriptor descriptor) {
		super(message);
		this.path = path;
		this.descriptor = descriptor;
	}

	public ValidationError(Throwable cause, IncludePath path) {
		super(cause);
		this.path = path;
	}

	public ValidationError(Throwable cause, IncludePath path, JsonDescriptor descriptor) {
		super(cause);
		this.path = path;
		this.descriptor = descriptor;
	}

	public IncludePath getPath() {
		return path;
	}

	public JsonDescriptor getDescriptor() {
		return descriptor;
	}

}
