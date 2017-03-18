package com.untrackr.alerter.processor.primitives.special.parallel;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Parallel extends Processor<ParallelDescriptor> {

	private List<Processor> processors;

	public Parallel(ProcessorService processorService, List<Processor> processors, ParallelDescriptor descriptor, String name) {
		super(processorService, descriptor, name);
		this.processors = processors;
		for (Processor processor : processors) {
			processor.assignContainer(this);
		}
	}

	@Override
	public void addProducer(Processor producer) {
		for (Processor processor : processors) {
			processor.addProducer(producer);
		}
	}

	@Override
	public void addConsumer(Processor consumer) {
		for (Processor processor : processors) {
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

	public void inferSignature() {
		signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.any, ProcessorSignature.PipeRequirement.any);
		for (Processor processor : processors) {
			ProcessorSignature bottomSignature = signature.bottom(processor.getSignature());
			if (bottomSignature == null) {
				String message = "signature of " + processor.getType() + " is " + processor.getSignature().describe() + " and is inconsistent with previous processors in parallel group: " + signature.describe();
				throw new AlerterException(message, ExceptionContext.makeProcessorFactory(processor.getType()));
			}
			signature = bottomSignature;
		}
	}

}
