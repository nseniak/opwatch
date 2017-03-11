package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Consumer<D extends ActiveProcessorDesc> extends ActiveProcessor<D> {

	public Consumer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
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
