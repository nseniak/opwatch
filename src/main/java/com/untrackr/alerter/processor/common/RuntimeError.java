package com.untrackr.alerter.processor.common;

public class RuntimeError extends RuntimeException {

	private static Message.Level DEFAULT_LEVEL = Message.Level.emergency;

	private ExecutionScope scope;
	private Message.Level level;

	public RuntimeError(String message) {
		super(message);
		this.level = DEFAULT_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(String message, ExecutionScope scope) {
		super(message);
		this.level = DEFAULT_LEVEL;
		this.scope = scope;
	}


	public RuntimeError(String message, Throwable cause) {
		super(message, cause);
		this.level = DEFAULT_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(String message, ExecutionScope scope, Throwable cause) {
		super(message, cause);
		this.level = DEFAULT_LEVEL;
		this.scope = scope;
	}


	public RuntimeError(Throwable cause) {
		super(cause);
		this.level = DEFAULT_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(Throwable cause, ExecutionScope scope) {
		super(cause);
		this.level = DEFAULT_LEVEL;
		this.scope = scope;
	}

	public ExecutionScope getScope() {
		return scope;
	}

	public Message.Level getLevel() {
		return level;
	}

	public void setLevel(Message.Level level) {
		this.level = level;
	}

}
