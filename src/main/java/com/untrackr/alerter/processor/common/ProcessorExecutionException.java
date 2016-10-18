package com.untrackr.alerter.processor.common;

public class ProcessorExecutionException extends AlerterException {

	public ProcessorExecutionException(String message) {
		this(message, null, null);
	}

	public ProcessorExecutionException(String message, Processor processor) {
		this(message, processor, null);
	}

	public ProcessorExecutionException(String message, Processor processor, Payload payload) {
		super(message, processor, payload);
	}

	public ProcessorExecutionException(Throwable cause, Processor processor, Payload payload) {
		super(cause, processor, payload);
	}

}
