package com.untrackr.alerter.processor.transformer.identity;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorDesc;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Transformer {

	public Identity(ProcessorService processorService, ProcessorDesc descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consume(Payload input) {
		outputTransformed(input.getValue(), input);
	}

}
