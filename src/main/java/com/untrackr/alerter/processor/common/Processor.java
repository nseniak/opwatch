package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public abstract class Processor {

	protected ProcessorService processorService;
	protected IncludePath path;
	protected ProcessorSignature signature;
	protected List<Processor> producers = new ArrayList<>();
	protected List<Processor> consumers = new ArrayList<>();

	public Processor(ProcessorService processorService, IncludePath path) {
		this.processorService = processorService;
		this.path = path;
	}

	public void addProducer(Processor producer) {
		producers.add(producer);
		producer.addConsumer(this);
	}

	public void addConsumer(Processor consumer) {
		consumers.add(consumer);
	}

	public void check() throws ValidationError {
		StringJoiner joiner = new StringJoiner(", ");
		switch (signature.getInputRequirement()) {
			case required:
				if (producers.isEmpty()) {
					joiner.add("consumer not receiving any input");
				}
				break;
			case forbidden:
				if (!producers.isEmpty()) {
					joiner.add("non-consumer receives an input");
				}
				break;
			case any:
				break;
		}
		switch (signature.getOutputRequirement()) {
			case required:
				if (consumers.isEmpty()) {
					joiner.add("producer's output doesn't have any consumers");
				}
				break;
			case forbidden:
				if (!consumers.isEmpty()) {
					joiner.add("non-producer has consumers");
				}
				break;
			case any:
				break;
		}
		if (joiner.length() != 0) {
			throw new ValidationError("incorrect pipe: " + joiner.toString(), path);
		}
	}

	public void output(Object object, Payload input) {
		Payload payload = new Payload(processorService, this, object, input);
		payload.getJsonObject().put("timestamp", new Date());
		processorService.consumeConcurrently(consumers, payload);
	}

	public abstract void initialize();

	public abstract void consume(Payload payload);

	public String descriptor() {
		// Default
		return getClass().getSimpleName().toLowerCase() + "{}";
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public IncludePath getPath() {
		return path;
	}

	public List<Processor> getProducers() {
		return producers;
	}

	public List<Processor> getConsumers() {
		return consumers;
	}

	public ProcessorSignature getSignature() {
		return signature;
	}

}
