package com.untrackr.alerter.processor.common;

public class AlerterException extends RuntimeException {

	private Processor processor;
	private Payload payload;
	private boolean silent;

	public AlerterException(String message) {
		super(message);
	}

	public AlerterException(String message, Processor processor, Payload payload) {
		super(message);
		this.processor = processor;
		this.payload = payload;
	}

	public AlerterException(Throwable cause, Processor processor, Payload payload) {
		super(cause.getMessage(), cause);
		this.processor = processor;
		this.payload = payload;
	}

	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
