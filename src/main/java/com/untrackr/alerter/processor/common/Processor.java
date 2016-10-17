package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

import java.util.List;
import java.util.concurrent.Future;

public abstract class Processor {

	protected ProcessorService processorService;
	protected ScriptStack stack;
	protected ProcessorSignature signature;
	protected ConsumerThreadRunner consumerThreadRunner;
	protected Future<?> consumerThreadFuture;

	public Processor(ProcessorService processorService, ScriptStack stack) {
		this.processorService = processorService;
		this.stack = stack;
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

	public String type() {
		// Default
		return getClass().getSimpleName().toLowerCase();
	}

	public String descriptor() {
		return type() + "{}";
	}

	public String processorDescriptor() {
		StringBuilder builder = new StringBuilder();
		builder.append(stack.asString()).append(" > ").append(descriptor());
		return builder.toString();
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public ScriptStack getStack() {
		return stack;
	}

	public ProcessorSignature getSignature() {
		return signature;
	}

	public ConsumerThreadRunner getConsumerThreadRunner() {
		return consumerThreadRunner;
	}

}
