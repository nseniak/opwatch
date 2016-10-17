package com.untrackr.alerter.processor.transformer.identity;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Identity extends Transformer {

	public Identity(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
	}

	@Override
	public void consume(Payload input) {
		outputTransformed(input.getScriptObject(), input);
	}

}
