package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import com.untrackr.alerter.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.Future;

/**
 * A processor that runs in its own thread
 */
public abstract class ActiveProcessor<D extends ActiveProcessorConfig> extends Processor<D> {

	private static final Logger logger = LoggerFactory.getLogger(ActiveProcessor.class);

	private Future<?> consumerThreadFuture;
	private long lastOutputTime = 0;
	private ConsumerThreadRunner consumerThreadRunner;

	public ActiveProcessor(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void inferSignature() {
		ActiveProcessorFactory<?, ?> factory = (ActiveProcessorFactory<?, ?>) processorService.getScriptService().factory(this.getClass());
		if (factory == null) {
			throw new IllegalStateException("cannot find factory for class " + this.getClass().getName());
		}
		signature = factory.staticSignature();
		if (signature == null) {
			throw new IllegalStateException("no static signature for class " + this.getClass().getName());
		}
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		producer.addConsumer(this);
	}

	// TODO Signal error in factory?
	@Override
	public void check() {
		StringJoiner joiner = new StringJoiner(", ");
		switch (signature.getInputRequirement()) {
			case Data:
				if (producers.isEmpty()) {
					joiner.add("input is missing");
				}
				break;
			case NoData:
				if (!producers.isEmpty()) {
					joiner.add("should not have an input");
				}
				break;
			case Any:
				break;
		}
		switch (signature.getOutputRequirement()) {
			case Data:
				if (consumers.isEmpty()) {
					joiner.add("output is ignored");
				}
				break;
			case NoData:
				if (!consumers.isEmpty()) {
					joiner.add("has no output but expected to have one");
				}
				break;
			case Any:
				break;
		}
		if (joiner.length() != 0) {
			throw new RuntimeError(joiner.toString(), new ProcessorVoidExecutionScope(this));
		}
	}

	public <V> void outputTransformed(V value, Payload<?> input) {
		Payload<V> payload = Payload.makeTransformed(processorService, this, input, value);
		output(consumers, payload);
	}

	public <V> void outputProduced(V value) {
		Payload<V> payload = Payload.makeRoot(processorService, this, value);
		output(consumers, payload);
	}

	public abstract void consumeInOwnThread(Payload<?> payload);

	public void output(Payload<?> payload) {
		output(consumers, payload);
	}

	private void output(List<Processor<?>> consumers, Payload<?> payload) {
		if (processorService.config().trace()) {
			logger.info("Output: " + getName() + " ==> " + processorService.json(payload));
		}
		long now = System.currentTimeMillis();
		long elapsedSinceLastOutput = now - lastOutputTime;
		long minElapsed = processorService.config().minimumOutputDelay();
		if (elapsedSinceLastOutput < minElapsed) {
			try {
				Thread.sleep(minElapsed - elapsedSinceLastOutput);
			} catch (InterruptedException e) {
				throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
			}
		}
		for (Processor<?> consumer : consumers) {
			consumer.consume(payload);
		}
		lastOutputTime = System.currentTimeMillis();
	}

	public void createConsumerThread() {
		consumerThreadRunner = new ConsumerThreadRunner(processorService, this);
		consumerThreadFuture = processorService.getConsumerExecutor().submit(consumerThreadRunner);
	}

	public void stopConsumerThread() {
		if (!consumerThreadFuture.isDone()) {
			boolean stopped = consumerThreadFuture.cancel(true);
			if (!stopped) {
				throw new RuntimeError("cannot stop consumer thread", new ProcessorVoidExecutionScope(this));
			}
		}
	}

	@Override
	public void consume(Payload<?> payload) {
		consumerThreadRunner.consume(payload);
	}

	public <T> T payloadValue(Payload payload, Class<?> clazz) {
		Object value = payload.getValue();
		if (!clazz.isAssignableFrom(value.getClass())) {
			ScriptService sc = processorService.getScriptService();
			String message = "wrong input value: expected " + sc.typeName(clazz) + ", got " + sc.typeName(value.getClass());
			throw new RuntimeError(message, new ProcessorPayloadExecutionScope(this, payload));
		}
		return (T) value;
	}

}
