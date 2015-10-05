package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

import java.util.List;
import java.util.concurrent.Future;

public abstract class Processor {

	protected ProcessorService processorService;
	protected IncludePath path;
	protected ProcessorSignature signature;
	protected ConsumerThreadRunner consumerThreadRunner;
	protected Future<?> consumerThreadFuture;

	public Processor(ProcessorService processorService, IncludePath path) {
		this.processorService = processorService;
		this.path = path;
	}

	public abstract void addProducer(Processor producer);

	public abstract void addConsumer(Processor consumer);

	public abstract void start();

	public abstract void stop();

	public abstract boolean started();

	public abstract boolean stopped();

	public abstract void consume(Payload payload);

	public abstract void check() throws ValidationError;

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

	public String pathDescriptor() {
		StringBuilder builder = new StringBuilder();
		builder.append(path.pathDescriptor()).append(" > ").append(descriptor());
		return builder.toString();
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public IncludePath getPath() {
		return path;
	}

	public ProcessorSignature getSignature() {
		return signature;
	}

	public ConsumerThreadRunner getConsumerThreadRunner() {
		return consumerThreadRunner;
	}

}
