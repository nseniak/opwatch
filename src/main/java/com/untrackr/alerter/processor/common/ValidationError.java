package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;

public class ValidationError extends RuntimeException {

	private IncludePath path;
	private JsonObject descriptor;

	public ValidationError(String message, IncludePath path) {
		super(message);
		this.path = path;
	}

	public ValidationError(String message, IncludePath path, JsonObject descriptor) {
		super(message);
		this.path = path;
		this.descriptor = descriptor;
	}

	public ValidationError(Throwable cause, IncludePath path) {
		super(cause);
		this.path = path;
	}

	public ValidationError(Throwable cause, IncludePath path, JsonObject descriptor) {
		super(cause);
		this.path = path;
		this.descriptor = descriptor;
	}

	public IncludePath getPath() {
		return path;
	}

	public JsonObject getDescriptor() {
		return descriptor;
	}

}
