package com.untrackr.alerter.processor.primitives.filter;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalFilter<D extends ActiveProcessorDescriptor> extends Filter<D> {

	public ConditionalFilter(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consumeInOwnThread(Payload<?> input) {
		if (predicateValue(input)) {
			outputTransformed(input.getValue(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
