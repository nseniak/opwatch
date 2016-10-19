package com.untrackr.alerter.processor.common;

public class AlerterException extends RuntimeException {

	/**
	 * Exception context
	 */
	private ExceptionContext exceptionContext;
	/**
	 * True if the exception should not be signaled as an alert
	 */
	private boolean silent;

	public AlerterException(String message, ExceptionContext exceptionContext) {
		super(message);
		this.exceptionContext = exceptionContext;
	}

	public AlerterException(Throwable cause, ExceptionContext exceptionContext) {
		super(cause);
		this.exceptionContext = exceptionContext;
	}

	public ExceptionContext getExceptionContext() {
		return exceptionContext;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
