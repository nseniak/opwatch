package com.untrackr.alerter.processor.primitives.transformer.identity;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Transformer<IdentityDescriptor> {

	public Identity(ProcessorService processorService, IdentityDescriptor descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consume(Payload input) {
		outputTransformed(input.getValue(), input);
	}

}
