package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public abstract class ActiveProcessor extends Processor {

	protected List<Processor> producers = new ArrayList<>();
	protected List<Processor> consumers = new ArrayList<>();
	private long lastOutputTime = 0;
	private String name;

	public ActiveProcessor(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
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

	public void check() throws ValidationError {
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
			throw new ValidationError("incorrect pipe: " + joiner.toString(), path);
		}
	}

	@Override
	public String descriptor() {
		String id = identifier();
		if (id == null) {
			return super.descriptor();
		} else {
			return type() + "{'" + id + "'}";
		}
	}

	public void outputFiltered(Object object, Payload input) {
		Payload payload = Payload.makeFiltered(processorService, this, object, input);
		output(consumers, payload);
	}

	public void outputProduced(Object object) {
		Object jsonObject = (object instanceof Map) ? object : processorService.getObjectMapper().convertValue(object, Map.class);
		Payload payload = Payload.makeRoot(processorService, this, jsonObject);
		output(consumers, payload);
	}

	public void output(List<Processor> consumers, Payload payload) {
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
		processorService.getConsumerExecutor().execute(consumerThreadRunner);
	}

	public <T> T payloadFieldValue(Payload input, String fieldName, Class<T> clazz) {
		Object jsonObject = input.getJsonObject();
		if (!(jsonObject instanceof Map)) {
			throw new RuntimeProcessorError("cannot get field value \"" + fieldName + "\" from non-object", this, input);
		}
		Object value = ((Map) jsonObject).get(fieldName);
		if (value == null) {
			throw new RuntimeProcessorError("mising field value \"" + fieldName + "\"", this, input);
		}
		if (!clazz.isAssignableFrom(value.getClass())) {
			throw new RuntimeProcessorError("wrong type for field \"" + fieldName + "\"", this, input);
		}
		return (T) value;
	}

	public  String identifier() {
		// Default
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
