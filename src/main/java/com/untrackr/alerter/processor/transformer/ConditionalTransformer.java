package com.untrackr.alerter.processor.transformer;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorDesc;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalTransformer extends Transformer {

	public ConditionalTransformer(ProcessorService processorService, ProcessorDesc descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consume(Payload input) {
		if (predicateValue(input)) {
			outputTransformed(input.getValue(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
