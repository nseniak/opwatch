package com.untrackr.alerter.processor.transformer;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalTransformer<D extends ActiveProcessorDesc> extends Transformer<D> {

	public ConditionalTransformer(ProcessorService processorService, D descriptor, String name) {
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
