package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ConditionalFilter extends Filter {

	public ConditionalFilter(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
	}

	@Override
	public void consume(Payload input) {
		if (predicateValue(input)) {
			outputFiltered(input.getScriptObject(), input);
		}
	}

	public abstract boolean predicateValue(Payload input);

}
