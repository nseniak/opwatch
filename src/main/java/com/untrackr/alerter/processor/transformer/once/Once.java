package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Once extends Transformer<OnceDesc> {

	private long delay;
	private long lastInputTimestamp = 0;

	public Once(ProcessorService processorService, OnceDesc descriptor, String name, long delay) {
		super(processorService, descriptor, name);
		this.delay = delay;
	}

	@Override
	public void consume(Payload payload) {
		if ((payload.getTimestamp() - lastInputTimestamp) > delay) {
			outputTransformed(payload.getValue(), payload);
		}
		lastInputTimestamp = payload.getTimestamp();
	}

}
