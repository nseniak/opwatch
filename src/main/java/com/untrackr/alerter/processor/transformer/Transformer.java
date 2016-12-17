package com.untrackr.alerter.processor.transformer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorDesc;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Transformer extends ActiveProcessor {

	public Transformer(ProcessorService processorService, ProcessorDesc descriptor, String name) {
		super(processorService, descriptor, name);
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
