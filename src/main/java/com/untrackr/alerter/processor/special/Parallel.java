package com.untrackr.alerter.processor.special;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
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
		producers.add(producer);
		for (Processor processor : processors) {
			processor.addProducer(producer);
		}
	}

	@Override
	public void addConsumer(Processor consumer) {
		consumers.add(consumer);
		for (Processor processor : processors) {
			processor.addConsumer(consumer);
		}
	}

	@Override
	public void initialize() {
		processorService.initializeConcurrently(processors);
	}

	@Override
	public void check() throws ValidationError {
		for (Processor processor : processors) {
			processor.check();
		}
		super.check();
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

	public void inferSignature(JsonObject jsonObject, IncludePath path) throws ValidationError {
		signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.any, ProcessorSignature.PipeRequirement.any);
		for (Processor processor : processors) {
			ProcessorSignature bottomSignature = signature.bottom(processor.getSignature());
			if (bottomSignature == null) {
				throw new ValidationError("signature of " + processor.descriptor() + " inconsistent with other parallel processors: " + processor.getSignature().describe(), path, jsonObject);
			}
			signature = bottomSignature;
		}
	}

}
