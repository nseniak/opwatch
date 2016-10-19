package com.untrackr.alerter.processor.transformer.identity;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Transformer {

	public Identity(ProcessorService processorService, String name) {
		super(processorService, name);
	}

	@Override
	public void consume(Payload input) {
		outputTransformed(input.getScriptObject(), input);
	}

}
