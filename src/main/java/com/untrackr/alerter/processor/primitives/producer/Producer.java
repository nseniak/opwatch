package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Producer<D extends ActiveProcessorDescriptor> extends ActiveProcessor<D> {

	public Producer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
		this.signature = ProcessorSignature.makeProducer();
	}

	@Override
	public void consume(Payload payload) {
		// Ignore input
	}

}
