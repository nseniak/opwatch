package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;
import org.javatuples.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public abstract class Processor {

	protected ProcessorService processorService;
	protected String name;
	protected ProcessorLocation location;
	protected ProcessorSignature signature;
	protected ConsumerThreadRunner consumerThreadRunner;
	protected Future<?> consumerThreadFuture;
	private Set<JavascriptFunction> scriptErrorSignaled = new HashSet<>();
	private Set<Pair<Processor, String>> propertyErrorSignaled = new HashSet<>();

	public Processor(ProcessorService processorService, String name) {
		this.processorService = processorService;
		this.name = name;
		this.location = new ProcessorLocation(name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
