package com.untrackr.alerter.processor.filter.identity;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Filter {

	public Identity(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
	}

	@Override
	public void consume(Payload input) {
		outputFiltered(input.getScriptObject(), input);
	}

}
