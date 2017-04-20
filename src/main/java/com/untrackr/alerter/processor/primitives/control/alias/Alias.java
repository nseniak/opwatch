package com.untrackr.alerter.processor.primitives.control.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ControlProcessor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class Alias extends ControlProcessor<AliasConfig> {

	Processor<?> processor;

	public Alias(ProcessorService processorService, Processor<?> processor, AliasConfig configuration, String name) {
		super(processorService, configuration, name);
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
	public void check() {
		processor.check();
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. The processor is already connected.
	}

	@Override
	public void inferSignature() {
		processor.inferSignature();
		this.signature = processor.getSignature();
	}

}
