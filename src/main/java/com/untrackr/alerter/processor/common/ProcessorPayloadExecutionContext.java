package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class ProcessorPayloadExecutionContext extends ProcessorExecutionContext {

	private Payload<?> payload;

	public ProcessorPayloadExecutionContext(Processor<?> processor, Payload<?> payload) {
		super(processor);
		this.payload = payload;
	}

	@Override
	public void addContextData(MessageData data, ProcessorService processorService) {
		super.addContextData(data, processorService);
		data.put("payload", processorService.json(payload));
	}

	public Payload<?> getPayload() {
		return payload;
	}

}
