package com.untrackr.alerter.processor.primitives.control.parallel;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Parallel extends ControlProcessor<ParallelConfig> {

	private List<Processor<?>> processors;

	public Parallel(ProcessorService processorService, List<Processor<?>> processors, ParallelConfig descriptor, String name) {
		super(processorService, descriptor, name);
		this.processors = processors;
		for (Processor<?> processor : processors) {
			processor.assignContainer(this);
		}
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		for (Processor<?> processor : processors) {
			processor.addProducer(producer);
		}
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		for (Processor<?> processor : processors) {
			processor.addConsumer(consumer);
		}
	}

	@Override
	public void start() {
		for (Processor processor : processors) {
			processor.start();
		}
	}

	@Override
	public void stop() {
		stop(processors);
	}

	@Override
	public void check() {
		for (Processor processor : processors) {
			processor.check();
		}
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

	@Override
	public void inferSignature() {
		for (Processor<?> processor : processors) {
			processor.inferSignature();
		}
		signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.Any, ProcessorSignature.PipeRequirement.Any);
		Processor<?> previousProcessor = null;
		int index = 1;
		for (Processor<?> processor : processors) {
			ProcessorSignature bottomSignature = signature.bottom(processor.getSignature());
			String incompatible = null;
			if (bottomSignature.getInputRequirement() == ProcessorSignature.PipeRequirement.None) {
				incompatible = "inputs";
			} else if (bottomSignature.getOutputRequirement() == ProcessorSignature.PipeRequirement.None) {
				incompatible = "outputs";
			}
			if (incompatible != null) {
				String message = incompatible + " of processors #" + (index - 1) + " (" + previousProcessor.getName() + ") and #" + index + " (" + processor.getName() + ") are incompatible";
				throw new RuntimeError(message, new ProcessorVoidExecutionScope(this));
			}
			signature = bottomSignature;
			previousProcessor = processor;
			index = index + 1;
		}
	}

}
