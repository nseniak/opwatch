package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Stdout extends Filter<StdoutConfig> {

	private boolean displayPayload;

	public Stdout(ProcessorService processorService, StdoutConfig descriptor, String name, boolean displayPayload) {
		super(processorService, descriptor, name);
		this.displayPayload = displayPayload;
	}

	@Override
	public void inferSignature() {
		// Override signature
		this.signature = ProcessorSignature.makeSideEffectConsumer();
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		System.out.println(processorService.json(displayPayload ? payload : payload.getValue()));
		outputTransformed(payload.getValue(), payload);
	}

}
