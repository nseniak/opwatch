package com.untrackr.alerter.processor.common;

public class RuntimeError extends RuntimeException {

	private static Message.Level DEFAULT_LEVEL = Message.Level.emergency;

	private ExecutionContext context;
	private Message.Level level;

	public RuntimeError(String message, ExecutionContext context) {
		super(message);
		this.level = DEFAULT_LEVEL;
		this.context = context;
	}

	public RuntimeError(String message) {
		super(message);
		this.level = DEFAULT_LEVEL;
		this.context = new GlobalExecutionContext();
	}

	public RuntimeError(Throwable cause, ExecutionContext context) {
		super(cause);
		this.level = DEFAULT_LEVEL;
		this.context = context;
	}

	public RuntimeError(Throwable cause) {
		super(cause);
		this.level = DEFAULT_LEVEL;
		this.context = new GlobalExecutionContext();
	}

	public RuntimeError(String message, Throwable cause, ExecutionContext context) {
		super(message, cause);
		this.level = DEFAULT_LEVEL;
		this.context = context;
	}

	public RuntimeError(String message, Throwable cause) {
		super(message, cause);
		this.level = DEFAULT_LEVEL;
		this.context = new GlobalExecutionContext();
	}

	public ExecutionContext getContext() {
		return context;
	}

	public Message.Level getLevel() {
		return level;
	}

	public void setLevel(Message.Level level) {
		this.level = level;
	}

}
