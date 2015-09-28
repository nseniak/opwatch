package com.untrackr.alerter.processor.special;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Pipe extends Processor {

	List<Processor> processors;

	public Pipe(ProcessorService processorService, List<Processor> processors, IncludePath path) {
		super(processorService, path);
		this.processors = processors;
		this.signature = new ProcessorSignature(first().getSignature().getInputRequirement(), last().getSignature().getOutputRequirement());
		Processor previousProducer = null;
		for (Processor processor : processors) {
			if (previousProducer == null) {
				previousProducer = processor;
			} else {
				processor.addProducer(previousProducer);
				previousProducer = processor;
			}
		}
	}

	@Override
	public void addProducer(Processor producer) {
		super.addProducer(producer);
		first().addProducer(producer);
	}

	@Override
	public void addConsumer(Processor consumer) {
		super.addConsumer(consumer);
		last().addConsumer(consumer);
	}

	@Override
	public void check() throws ValidationError {
		for (Processor processor : processors) {
			processor.check();
		}
		super.check();
	}

	@Override
	public void initialize() {
		for (int i = processors.size() - 1; i >= 0; i--) {
			Processor processor = processors.get(i);
			processorService.withErrorHandling(processor, null, processor::initialize);
		}
	}

	private Processor first() {
		return processors.get(0);
	}

	private Processor last() {
		return processors.get(processors.size() -1);
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

}
