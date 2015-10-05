package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public abstract class ActiveProcessor extends Processor {

	private static final Logger logger = LoggerFactory.getLogger(ActiveProcessor.class);

	protected List<Processor> producers = new ArrayList<>();
	protected List<Processor> consumers = new ArrayList<>();
	private long lastOutputTime = 0;
	private String name;
	protected boolean started;

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

	@Override
	public void start() {
		doStart();
		started = true;
	}

	protected abstract void doStart();

	@Override
	public void stop() {
		if (started) {
			doStop();
			started = false;
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
		Payload payload = Payload.makeRoot(processorService, this, object);
		output(consumers, payload);
	}

	public void output(List<Processor> consumers, Payload payload) {
		if (processorService.getProfileService().profile().isTrace()) {
			logger.info("Output: " + pathDescriptor() + " ==> " + payload.asText());
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
			throw new RuntimeProcessorError("cannot stop consumer thread", this, null);
		}
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

	public Object runScript(CompiledScript value, Bindings bindings, Payload payload) {
		// Copy the input because the js code might do side effects on it
		bindings.put("input", payload.getJsonObject());
		try {
			return value.eval(bindings);
		} catch (ScriptException e) {
			throw new RuntimeProcessorError(e, this, payload);
		}
	}

	public String identifier() {
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
