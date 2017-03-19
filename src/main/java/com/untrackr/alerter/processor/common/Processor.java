package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.descriptor.JavascriptFunction;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Processor<D extends ProcessorDescriptor> {

	protected ProcessorService processorService;
	protected D descriptor;
	protected String type;
	protected List<Processor<?>> producers = new ArrayList<>();
	protected List<Processor<?>> consumers = new ArrayList<>();
	protected Processor<?> container;
	protected ProcessorSignature signature;
	protected ProcessorLocation location;
	private Set<JavascriptFunction> scriptErrorSignaled = new HashSet<>();
	private Set<Pair<Processor, String>> propertyErrorSignaled = new HashSet<>();
	private boolean typeErrorSignaled = false;

	public Processor(ProcessorService processorService, D descriptor, String type) {
		this.processorService = processorService;
		this.type = type;
		this.descriptor = descriptor;
		this.location = new ProcessorLocation(type);
	}

	public void assignContainer(Processor<?> processor) {
		if (container != null) {
			throw new AlerterException("processor already used by " + container.getLocation().descriptor(), ExceptionContext.makeProcessorNoPayload(this));
		}
		container = processor;
	}

	public void addProducer(Processor<?> producer) {
		producers.add(producer);
	}

	public void addConsumer(Processor<?> consumer) {
		consumers.add(consumer);
	}

	public abstract void start();

	public abstract void stop();

	public abstract boolean started();

	public abstract boolean stopped();

	public abstract void consume(Payload<?> payload);

	public abstract void inferSignature();

	public abstract void check();

	public boolean allStarted(List<Processor<?>> processors) {
		return processors.stream().allMatch(Processor::started);
	}

	public boolean allStopped(List<Processor<?>> processors) {
		return processors.stream().allMatch(Processor::stopped);
	}

	public boolean scriptErrorSignaled(JavascriptFunction function) {
		return !scriptErrorSignaled.add(function);
	}

	public boolean propertyErrorSignaled(String propertyName) {
		return !propertyErrorSignaled.add(new Pair<>(this, propertyName));
	}

	public boolean typeErrorSignaled() {
		boolean signaled = typeErrorSignaled;
		typeErrorSignaled = true;
		return signaled;
	}

	@Override
	public String toString() {
		return "[object " + type + "]";
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public ProcessorLocation getLocation() {
		return location;
	}

	public ProcessorSignature getSignature() {
		return signature;
	}

	public String getType() {
		return type;
	}

	public D getDescriptor() {
		return descriptor;
	}

}
