package com.untrackr.alerter.processor.filter.identity;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Filter {

	public Identity(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void consume(Payload input) {
		outputFiltered(input.getJsonObject(), input);
	}

}
