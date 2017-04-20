package com.untrackr.alerter.processor.primitives.filter;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalFilter<D extends ActiveProcessorConfig> extends Filter<D> {

	public ConditionalFilter(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload<?> input) {
		if (predicateValue(input)) {
			outputTransformed(input.getValue(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
