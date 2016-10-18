package com.untrackr.alerter.processor.common;

public class ProcessorExecutionException extends AlerterException {

	private Processor processor;
	private Payload payload;
	private boolean silent;

	public ProcessorExecutionException(String message, Processor processor) {
		this(message, processor, null);
	}

	public ProcessorExecutionException(String message, Processor processor, Payload payload) {
		super(message);
		this.processor = processor;
		this.payload = payload;
	}

	public ProcessorExecutionException(Throwable cause, Processor processor, Payload payload) {
		super(cause);
		this.processor = processor;
		this.payload = payload;
	}

	public ProcessorExecutionException(Throwable t, Processor processor) {
		this(t, processor, null);
	}

	public Processor getProcessor() {
		return processor;
	}

	public Payload getPayload() {
		return payload;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
