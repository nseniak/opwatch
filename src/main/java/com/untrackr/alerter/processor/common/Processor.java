package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;
import org.javatuples.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public abstract class Processor<D extends ProcessorDesc> {

	protected ProcessorService processorService;
	protected D descriptor;
	protected String type;
	protected ProcessorLocation location;
	protected ProcessorSignature signature;
	protected Processor container;
	protected ConsumerThreadRunner consumerThreadRunner;
	protected Future<?> consumerThreadFuture;
	private Set<JavascriptFunction> scriptErrorSignaled = new HashSet<>();
	private Set<Pair<Processor, String>> propertyErrorSignaled = new HashSet<>();
	private boolean typeErrorSignaled = false;

	public Processor(ProcessorService processorService, D descriptor, String type) {
		this.processorService = processorService;
		this.type = type;
		this.descriptor = descriptor;
		this.location = new ProcessorLocation(type);
	}

	public void assignContainer(Processor processor) {
		if (container != null) {
			throw new AlerterException("processor already used by " + container.getLocation().descriptor(), ExceptionContext.makeProcessorNoPayload(this));
		}
		container = processor;
	}

	public abstract void addProducer(Processor producer);

	public abstract void addConsumer(Processor consumer);

	public abstract void start();

	public abstract void stop();

	public abstract boolean started();

	public abstract boolean stopped();

	public abstract void consume(Payload payload);

	public abstract void check();

	public boolean allStarted(List<Processor> processors) {
		return processors.stream().allMatch(Processor::started);
	}

	public boolean allStopped(List<Processor> processors) {
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

	public ConsumerThreadRunner getConsumerThreadRunner() {
		return consumerThreadRunner;
	}

	public String getType() {
		return type;
	}

	public D getDescriptor() {
		return descriptor;
	}

}
