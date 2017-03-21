package com.untrackr.alerter.processor.primitives.filter;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Filter<D extends ActiveProcessorDescriptor> extends ActiveProcessor<D> {

	public Filter(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void inferSignature() {
		this.signature = ProcessorSignature.makeTransformer();
	}

	@Override
	public void start() {
		createConsumerThread();
	}

	@Override
	public void stop() {
		stopConsumerThread();
	}

}
