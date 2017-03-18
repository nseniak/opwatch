package com.untrackr.alerter.processor.primitives.transformer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Transformer<D extends ActiveProcessorDescriptor> extends ActiveProcessor<D> {

	public Transformer(ProcessorService processorService, D descriptor, String name) {
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
