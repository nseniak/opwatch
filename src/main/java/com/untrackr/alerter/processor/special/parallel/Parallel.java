package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class Parallel extends Processor {

	private List<Processor> processors;

	public Parallel(ProcessorService processorService, List<Processor> processors, IncludePath path) throws ValidationError {
		super(processorService, path);
		this.processors = processors;
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
		processors.forEach(processor -> processorService.withErrorHandling(processor, null, processor::start));
	}

	@Override
	public void stop() {
		processors.forEach(processor -> processorService.withErrorHandling(processor, null, processor::stop));
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
	public void check() throws ValidationError {
		for (Processor processor : processors) {
			processor.check();
		}
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

	public void inferSignature(JsonDescriptor jsonObject, IncludePath path) throws ValidationError {
		signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.any, ProcessorSignature.PipeRequirement.any);
		for (Processor processor : processors) {
			ProcessorSignature bottomSignature = signature.bottom(processor.getSignature());
			if (bottomSignature == null) {
				String message = "signature of " + processor.descriptor() + " is " + processor.getSignature().describe() + " and is inconsistent with previous processors in parallel group: " + signature.describe();
				throw new ValidationError(message, path, jsonObject);
			}
			signature = bottomSignature;
		}
	}

}
