package org.opwatch.processor.common;

import org.opwatch.processor.config.ProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.ScriptRuntime.UNDEFINED;

public abstract class Processor<C extends ProcessorConfig> {

	private String id;
	protected ProcessorService processorService;
	protected C configuration;
	protected String name;
	protected List<Processor<?>> producers = new ArrayList<>();
	protected List<Processor<?>> consumers = new ArrayList<>();
	protected Processor<?> container;
	protected ProcessorSignature signature;
	protected ScriptStack constructionStack;

	public static final String PROCESSOR_RUNNING_MESSAGE = "processor up and running";
	public static final String PROCESSOR_STOPPED_MESSAGE = "processor stopped";
	public static final String PROCESSOR_INTERRUPTED_MESSAGE = "processor interrupted";

	public Processor(ProcessorService processorService, C configuration, String name) {
		this.id = processorService.uuid();
		this.processorService = processorService;
		this.name = name;
		this.configuration = configuration;
		this.constructionStack = ScriptStack.currentStack();
	}

	public void assignContainer(Processor<?> processor) {
		if (container != null) {
			throw new RuntimeError("a processor can only be used once; this one is already used in " + container.getName(),
					new ProcessorVoidExecutionScope(this));
		}
		container = processor;
	}

	public Object run() {
		inferSignature();
		check();
		processorService.signalSystemInfo(PROCESSOR_RUNNING_MESSAGE);
		start();
		processorService.setRunningProcessorThread(Thread.currentThread());
		try {
			while (true) {
				Thread.sleep(TimeUnit.DAYS.toMillis(1));
			}
		} catch (InterruptedException e) {
			processorService.printStderr(PROCESSOR_INTERRUPTED_MESSAGE);
		} finally {
			processorService.setRunningProcessorThread(null);
		}
		stop();
		processorService.signalSystemInfo(PROCESSOR_STOPPED_MESSAGE);
		return UNDEFINED;
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

	public abstract void consume(Payload payload);

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

	public ScriptStack getConstructionStack() {
		return constructionStack;
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
