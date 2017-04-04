package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class Trace extends Consumer<TraceConfig> {

	private boolean displayPayload;

	public Trace(ProcessorService processorService, TraceConfig descriptor, String name, boolean displayPayload) {
		super(processorService, descriptor, name);
		this.displayPayload = displayPayload;
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		System.out.println(processorService.json(displayPayload ? payload : payload.getValue()));
		outputTransformed(payload.getValue(), payload);
	}

}
