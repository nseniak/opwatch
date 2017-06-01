package org.opwatch.processor.primitives.filter;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public abstract class ConditionalFilter<D extends ActiveProcessorConfig> extends Filter<D> {

	public ConditionalFilter(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload input) {
		if (predicateValue(input)) {
			outputTransformed(input.getValue(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
