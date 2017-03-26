package com.untrackr.alerter.processor.primitives.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Pipe extends Processor<PipeConfig> {

	private List<Processor<?>> processors;

	public Pipe(ProcessorService processorService, List<Processor<?>> processors, PipeConfig descriptor, String name) {
		super(processorService, descriptor, name);
		this.processors = processors;
		Processor<?> previous = null;
		for (Processor<?> processor : processors) {
			processor.assignContainer(this);
			if (previous != null) {
				processor.addProducer(previous);
			}
			previous = processor;
		}
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		first().addProducer(producer);
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		last().addConsumer(consumer);
	}

	@Override
	public void check() {
		for (Processor processor : processors) {
			processor.check();
		}
	}

	@Override
	public void start() {
		for (int i = processors.size() - 1; i >= 0; i--) {
			Processor processor = processors.get(i);
			processorService.withProcessorErrorHandling(processor, processor::start);
		}
	}

	@Override
	public void stop() {
		for (Processor processor : processors) {
			processorService.withProcessorErrorHandling(processor, processor::stop);
		}
	}

	private Processor<?> first() {
		return processors.get(0);
	}

	private Processor<?> last() {
		return processors.get(processors.size() - 1);
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

	@Override
	public void inferSignature() {
		for (Processor processor : processors) {
			processor.inferSignature();
		}
		this.signature = new ProcessorSignature(first().getSignature().getInputRequirement(), last().getSignature().getOutputRequirement());
	}

}
