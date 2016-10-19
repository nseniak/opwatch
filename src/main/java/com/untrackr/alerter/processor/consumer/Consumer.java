package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Consumer extends ActiveProcessor {

	public Consumer(ProcessorService processorService, String name) {
		super(processorService, name);
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
