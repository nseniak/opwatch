package org.opwatch.processor.common;

import org.opwatch.processor.payload.Payload;

public class ProcessorPayloadExecutionScope extends ProcessorExecutionScope {

	private Payload payload;

	public ProcessorPayloadExecutionScope(Processor<?> processor, Payload payload) {
		super(processor);
		this.payload = payload;
	}

	public Payload getPayload() {
		return payload;
	}

}
