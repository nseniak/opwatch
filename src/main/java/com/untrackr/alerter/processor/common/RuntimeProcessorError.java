package com.untrackr.alerter.processor.common;

public class RuntimeProcessorError extends RuntimeException {

	private Processor processor;
	private Payload payload;

	public RuntimeProcessorError(String message, Processor processor, Payload payload) {
		super(message);
		this.processor = processor;
		this.payload = payload;
	}

	public RuntimeProcessorError(Throwable t, Processor processor, Payload payload) {
		super(t);
		this.processor = processor;
		this.payload = payload;
	}

	public Processor getProcessor() {
		return processor;
	}

	public Payload getPayload() {
		return payload;
	}

}
