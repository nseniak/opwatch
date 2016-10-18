package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Pipe extends Processor {

	List<Processor> processors;

	public Pipe(ProcessorService processorService, List<Processor> processors, ScriptStack stack) {
		super(processorService, stack);
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
		first().addProducer(producer);
	}

	@Override
	public void addConsumer(Processor consumer) {
		last().addConsumer(consumer);
	}

	@Override
	public void check() throws RuntimeScriptException {
		for (Processor processor : processors) {
			processor.check();
		}
	}

	@Override
	public void start() {
		for (int i = processors.size() - 1; i >= 0; i--) {
			Processor processor = processors.get(i);
			processorService.withProcessorErrorHandling(processor, null, processor::start);
		}
	}

	@Override
	public void stop() {
		for (Processor processor : processors) {
			processorService.withProcessorErrorHandling(processor, null, processor::stop);
		}
	}

	@Override
	public boolean started() {
		return allStarted(processors);
	}

	@Override
	public boolean stopped() {
		return allStopped(processors);
	}

	private Processor first() {
		return processors.get(0);
	}

	private Processor last() {
		return processors.get(processors.size() - 1);
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

}
