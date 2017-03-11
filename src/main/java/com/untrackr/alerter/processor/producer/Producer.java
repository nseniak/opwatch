package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Producer<D extends ActiveProcessorDesc> extends ActiveProcessor<D> {

	public Producer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
		this.signature = ProcessorSignature.makeProducer();
	}

	@Override
	public void consume(Payload payload) {
		// Ignore input
	}

}
