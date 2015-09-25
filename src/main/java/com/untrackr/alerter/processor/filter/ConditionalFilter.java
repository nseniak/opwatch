package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalFilter extends Filter {

	public ConditionalFilter(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void initialize() {
		// Nothing to do
	}

	@Override
	public void consume(Payload input) {
		if (conditionValue(input)) {
			output(input.getJsonObject(), input);
		}
	}

	public abstract boolean conditionValue(Payload input);

}
