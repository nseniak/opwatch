package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.untrackr.alerter.service.ProcessorService;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES;

@JsonSerialize(using = Processor.ProcessorJsonSerializer.class)
public abstract class Processor<D extends ProcessorDesc> {

	protected ProcessorService processorService;
	protected D descriptor;
	protected String type;
	protected ProcessorLocation location;
	protected ProcessorSignature signature;
	protected Processor container;
	protected ConsumerThreadRunner consumerThreadRunner;
	protected Future<?> consumerThreadFuture;
	private Set<JavascriptFunction> scriptErrorSignaled = new HashSet<>();
	private Set<Pair<Processor, String>> propertyErrorSignaled = new HashSet<>();

	public Processor(ProcessorService processorService, D descriptor, String type) {
		this.processorService = processorService;
		this.type = type;
		this.descriptor = descriptor;
		this.location = new ProcessorLocation(type);
	}

	public void assignContainer(Processor processor) {
		if (container != null) {
			throw new AlerterException("processor already used by " + container.getLocation().descriptor(), ExceptionContext.makeProcessorNoPayload(this));
		}
		container = processor;
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

	// TODO generate e.g. tail({file:"/tmp/foo.log"}). Add a method toJSON (which pretty prints)
	@Override
	public String toString() {
		return "[object " + type + "]";
	}

	public String toSource() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		objectMapper.configure(QUOTE_FIELD_NAMES, false);
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return getType() + "(<cannot serialize>)";
		}
	}

	public static class ProcessorJsonSerializer extends StdSerializer<Processor> {

		public ProcessorJsonSerializer() {
			this(null);
		}

		public ProcessorJsonSerializer(Class<Processor> t) {
			super(t);
		}

		@Override
		public void serialize(
				Processor value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			jgen.writeRaw(value.getType());
			jgen.writeRaw("(");
			jgen.writeObject(value.descriptor);
			jgen.writeRaw(")");
		}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
