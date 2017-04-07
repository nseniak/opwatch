package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class ProcessorPayloadExecutionScope extends ProcessorExecutionScope {

	private Payload<?> payload;

	public ProcessorPayloadExecutionScope(Processor<?> processor, Payload<?> payload) {
		super(processor);
		this.payload = payload;
	}

	public Payload<?> getPayload() {
		return payload;
	}

}
