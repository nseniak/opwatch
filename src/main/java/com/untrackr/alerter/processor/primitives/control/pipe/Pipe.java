package com.untrackr.alerter.processor.primitives.control.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.common.ControlProcessor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Pipe extends ControlProcessor<PipeConfig> {

	private List<Processor<?>> processors;

	public Pipe(ProcessorService processorService, List<Processor<?>> processors, PipeConfig configuration, String name) {
		super(processorService, configuration, name);
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
			processor.start();
		}
	}

	@Override
	public void stop() {
		stop(processors);
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
