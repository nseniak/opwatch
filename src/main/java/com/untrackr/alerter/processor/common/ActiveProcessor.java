package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.RemotePayload;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public abstract class ActiveProcessor extends Processor {

	private static final Logger logger = LoggerFactory.getLogger(ActiveProcessor.class);

	protected List<Processor> producers = new ArrayList<>();
	protected List<Processor> consumers = new ArrayList<>();
	private long lastOutputTime = 0;
	protected boolean started;

	public ActiveProcessor(ProcessorService processorService, String name) {
		super(processorService, name);
	}

	@Override
	public void addProducer(Processor producer) {
		producers.add(producer);
		producer.addConsumer(this);
	}

	@Override
	public void addConsumer(Processor consumer) {
		consumers.add(consumer);
	}

	@Override
	public void start() {
		doStart();
		started = true;
	}

	protected abstract void doStart();

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

	public void check() {
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
			throw new AlerterException("incorrect pipe: " + joiner.toString(), ExceptionContext.makeProcessorNoPayload(this));
		}
	}

	public void outputTransformed(Object object, Payload input) {
		Payload payload = Payload.makeTransformed(processorService, this, object, input);
		output(consumers, payload);
	}

	public void outputProduced(Object object) {
		Payload payload = Payload.makeRoot(processorService, this, object);
		output(consumers, payload);
	}

	public void outputReceived(RemotePayload remotePayload) {
		Payload payload = Payload.makeRemote(processorService, this, remotePayload);
		output(consumers, payload);
	}

	public void output(List<Processor> consumers, Payload payload) {
		if (processorService.getProfileService().profile().isTrace()) {
			logger.info("Output: " + location.descriptor() + " ==> " + payload.asText());
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
		for (Processor consumer : consumers) {
			consumer.getConsumerThreadRunner().consume(payload);
		}
		lastOutputTime = System.currentTimeMillis();
	}

	public void createConsumerThread() {
		consumerThreadRunner = new ConsumerThreadRunner(processorService, this);
		consumerThreadFuture = processorService.getConsumerExecutor().submit(consumerThreadRunner);
	}

	public void stopConsumerThread() {
		boolean stopped = consumerThreadFuture.cancel(true);
		if (!stopped) {
			throw new AlerterException("cannot stop consumer thread", ExceptionContext.makeProcessorNoPayload(this));
		}
	}

	public <T> T payloadPropertyValue(Payload payload, String propertyName, Class<?> clazz) {
		Object jsonObject = payload.getScriptObject();
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
