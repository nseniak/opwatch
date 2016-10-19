package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Producer extends ActiveProcessor {

	public Producer(ProcessorService processorService, String name) {
		super(processorService, name);
		this.signature = ProcessorSignature.makeProducer();
	}

	@Override
	public void consume(Payload payload) {
		// Ignore input
	}

}
