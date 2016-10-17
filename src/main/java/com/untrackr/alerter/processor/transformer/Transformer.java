package com.untrackr.alerter.processor.transformer;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Transformer extends ActiveProcessor {

	public Transformer(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
		this.signature = ProcessorSignature.makeTransformer();
	}

	@Override
	public void doStart() {
		createConsumerThread();
	}

	@Override
	public void doStop() {
		stopConsumerThread();
	}

}
