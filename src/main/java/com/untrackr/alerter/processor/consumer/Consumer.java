package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Consumer extends ActiveProcessor {

	public Consumer(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
		this.signature = ProcessorSignature.makeConsumer();
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
