package com.untrackr.alerter.processor.primitives.transformer.once;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Once extends Transformer<OnceDescriptor> {

	private long delay;
	private long lastInputTimestamp = 0;

	public Once(ProcessorService processorService, OnceDescriptor descriptor, String name, long delay) {
		super(processorService, descriptor, name);
		this.delay = delay;
	}

	@Override
	public void doConsume(Payload<?> payload) {
		if ((payload.getTimestamp() - lastInputTimestamp) > delay) {
			outputTransformed(payload.getValue(), payload);
		}
		lastInputTimestamp = payload.getTimestamp();
	}

}
