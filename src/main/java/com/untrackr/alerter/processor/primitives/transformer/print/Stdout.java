package com.untrackr.alerter.processor.primitives.transformer.print;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Stdout extends Transformer<StdoutDescriptor> {

	private boolean displayPayload;

	public Stdout(ProcessorService processorService, StdoutDescriptor descriptor, String name, boolean displayPayload) {
		super(processorService, descriptor, name);
		this.displayPayload = displayPayload;
	}

	@Override
	public void inferSignature() {
		// Override signature
		this.signature = ProcessorSignature.makeSideEffectConsumer();
	}

	@Override
	public void doConsume(Payload<?> payload) {
		System.out.println(processorService.json(displayPayload ? payload : payload.getValue()));
		outputTransformed(payload.getValue(), payload);
	}

}
