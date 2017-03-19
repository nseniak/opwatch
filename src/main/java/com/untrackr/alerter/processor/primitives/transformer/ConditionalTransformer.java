package com.untrackr.alerter.processor.primitives.transformer;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalTransformer<D extends ActiveProcessorDescriptor> extends Transformer<D> {

	public ConditionalTransformer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void doConsume(Payload<?> input) {
		if (predicateValue(input)) {
			outputTransformed(input.getValue(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
