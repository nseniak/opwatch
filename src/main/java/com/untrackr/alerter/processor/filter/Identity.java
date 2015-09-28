package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Filter {

	public Identity(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void initialize() {
		// Do nothing
	}

	@Override
	public void consume(Payload input) {
		outputFiltered(input.getJsonObject(), input);
	}

}
