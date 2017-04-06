package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.List;

public abstract class Processor<C extends ProcessorConfig> {

	private String id;
	protected ProcessorService processorService;
	protected C configuration;
	protected String name;
	protected List<Processor<?>> producers = new ArrayList<>();
	protected List<Processor<?>> consumers = new ArrayList<>();
	protected Processor<?> container;
	protected ProcessorSignature signature;
	protected ProcessorLocation location;

	public Processor(ProcessorService processorService, C configuration, String name) {
		this.id = processorService.uuid();
		this.processorService = processorService;
		this.name = name;
		this.configuration = configuration;
		this.location = new ProcessorLocation(name, ScriptStack.currentStack());
	}

	// TODO Signal error in factory instead
	public void assignContainer(Processor<?> processor) {
		if (container != null) {
			throw new RuntimeError("a processor can only be used once; this one is already used in " + container.getLocation().descriptor(),
					new ProcessorVoidExecutionContext(this));
		}
		container = processor;
	}

	/**
	 * For use in scripts
	 */
	public boolean isprocessor() {
		return true;
	}

	public void addProducer(Processor<?> producer) {
		producers.add(producer);
	}

	public void addConsumer(Processor<?> consumer) {
		consumers.add(consumer);
	}

	public abstract void start();

	public abstract void stop();

	public abstract void consume(Payload<?> payload);

	public abstract void inferSignature();

	public abstract void check();

	@Override
	public String toString() {
		return "[object " + name + "]";
	}

	public String getId() {
		return id;
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

	public String getName() {
		return name;
	}

	public C getConfiguration() {
		return configuration;
	}

	public ProcessorFactory<?, ?> getFactory() {
		return processorService.getScriptService().factory(this.getClass());
	}

}
