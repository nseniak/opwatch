package com.untrackr.alerter.processor.primitives.special.parallel;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Parallel extends Processor<ParallelDescriptor> {

	private List<Processor<?>> processors;

	public Parallel(ProcessorService processorService, List<Processor<?>> processors, ParallelDescriptor descriptor, String name) {
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
		processors.forEach(processor -> processorService.withProcessorErrorHandling(processor, processor::start));
	}

	@Override
	public void stop() {
		processors.forEach(processor -> processorService.withProcessorErrorHandling(processor, processor::stop));
	}

	@Override
	public boolean started() {
		return allStarted(processors);
	}

	@Override
	public boolean stopped() {
		return allStopped(processors);
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
				String message = incompatible + " of processors #" + (index - 1) + " (" + previousProcessor.getType() + ") and #" + index + " (" + processor.getType() + ") are incompatible";
				throw new AlerterException(message, ExceptionContext.makeProcessorFactory(this.getType()));
			}
			signature = bottomSignature;
			previousProcessor = processor;
			index = index + 1;
		}
	}

}
