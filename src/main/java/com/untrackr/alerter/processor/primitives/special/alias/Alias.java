package com.untrackr.alerter.processor.primitives.special.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class Alias extends Processor<AliasDescriptor> {

	Processor<?> processor;

	public Alias(ProcessorService processorService, Processor<?> processor, AliasDescriptor descriptor, String name) {
		super(processorService, descriptor, name);
		this.processor = processor;
		processor.assignContainer(this);
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		processor.addProducer(producer);
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		processor.addConsumer(consumer);
	}

	@Override
	public void start() {
		processor.start();
	}

	@Override
	public void stop() {
		processor.stop();
	}

	@Override
	public boolean started() {
		return processor.started();
	}

	@Override
	public boolean stopped() {
		return processor.stopped();
	}

	@Override
	public void check() {
		processor.check();
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. The processor is already connected.
	}

	@Override
	public void inferSignature() {
		this.signature = processor.getSignature();
	}

}
