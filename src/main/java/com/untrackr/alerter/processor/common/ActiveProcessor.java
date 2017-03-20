package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import com.untrackr.alerter.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Future;

/**
 * A processor that runs in its own thread
 */
public abstract class ActiveProcessor<D extends ActiveProcessorDescriptor> extends Processor<D> {

	private static final Logger logger = LoggerFactory.getLogger(ActiveProcessor.class);

	private Future<?> consumerThreadFuture;
	private long lastOutputTime = 0;
	private boolean started;
	private ConsumerThreadRunner consumerThreadRunner;

	public ActiveProcessor(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		producer.addConsumer(this);
	}

	@Override
	public void start() {
		if (started) {
			throw new AlerterException("processor already running", ExceptionContext.makeProcessorNoPayload(this));
		}
		doStart();
		started = true;
	}

	protected abstract void doStart();

	public abstract void doConsume(Payload<?> payload);

	@Override
	public void stop() {
		if (started) {
			try {
				doStop();
			} finally {
				started = false;
			}
		}
	}

	@Override
	public boolean started() {
		return started;
	}

	@Override
	public boolean stopped() {
		return !started;
	}

	protected abstract void doStop();

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
			throw new AlerterException(joiner.toString(), ExceptionContext.makeProcessorNoPayload(this));
		}
	}

	public void outputTransformed(Object value, Payload input) {
		Payload payload = Payload.makeTransformed(processorService, this, input, value);
		output(consumers, payload);
	}

	public void outputProduced(Object value) {
		Payload payload = Payload.makeRoot(processorService, this, value);
		output(consumers, payload);
	}

	public void output(Payload<?> payload) {
		output(consumers, payload);
	}

	private void output(List<Processor<?>> consumers, Payload<?> payload) {
		if (processorService.getProfileService().profile().isTrace()) {
			logger.info("Output: " + location.descriptor() + " ==> " + processorService.json(payload));
		}
		long now = System.currentTimeMillis();
		long elapsedSinceLastOutput = now - lastOutputTime;
		long minElapsed = processorService.getProfileService().profile().getMinimumOutputDelay();
		if (elapsedSinceLastOutput < minElapsed) {
			try {
				Thread.sleep(minElapsed - elapsedSinceLastOutput);
			} catch (InterruptedException e) {
				// Shutting down
				return;
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
				throw new AlerterException("cannot stop consumer thread", ExceptionContext.makeProcessorNoPayload(this));
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
			AlerterException exception = new AlerterException("wrong input value: expected "
					+ sc.typeName(clazz) + ", got " + sc.typeName(value.getClass()), ExceptionContext.makeProcessorPayload(this, payload));
			exception.setSilent(typeErrorSignaled());
			throw exception;
		}
		return (T) value;
	}

	public <T> T payloadPropertyValue(Payload payload, String propertyName, Class<?> clazz) {
		Object jsonObject = payload.getValue();
		Object value = null;
		if (jsonObject instanceof Map) {
			value = ((Map) jsonObject).get(propertyName);
		} else {
			try {
				Field field = jsonObject.getClass().getDeclaredField(propertyName);
				field.setAccessible(true);
				value = field.get(jsonObject);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				// Do nothing
			}
		}
		if (value == null) {
			AlerterException exception = new AlerterException("missing field value \"" + propertyName + "\"", ExceptionContext.makeProcessorPayload(this, payload));
			exception.setSilent(propertyErrorSignaled(propertyName));
			throw exception;
		}
		if (!clazz.isAssignableFrom(value.getClass())) {
			AlerterException exception = new AlerterException("wrong type for field \"" + propertyName + "\"", ExceptionContext.makeProcessorPayload(this, payload));
			exception.setSilent(propertyErrorSignaled(propertyName));
			throw exception;
		}
		return (T) value;
	}

}
