package com.untrackr.alerter.processor.common;

public class AlerterException extends RuntimeException {

	public AlerterException(String message) {
		super(message);
	}

	public AlerterException(Throwable cause) {
		super(cause);
	}

}
