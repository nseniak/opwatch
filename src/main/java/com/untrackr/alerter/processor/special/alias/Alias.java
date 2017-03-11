package com.untrackr.alerter.processor.special.alias;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;

@JsonSerialize(using = Alias.MacroJsonSerializer.class)
public class Alias extends Processor<AliasDesc> {

	Processor processor;

	public Alias(ProcessorService processorService, Processor processor, AliasDesc descriptor, String name) {
		super(processorService, descriptor, name);
		this.processor = processor;
		this.signature = processor.getSignature();
		processor.assignContainer(this);
	}

	@Override
	public void addProducer(Processor producer) {
		processor.addProducer(producer);
	}

	@Override
	public void addConsumer(Processor consumer) {
		processor.addConsumer(consumer);
	}

	@Override
	public void start() {
		processor.start();
	}

	@Override
	public void stop() {
		processor.stop();
	}

	@Override
	public boolean started() {
		return processor.started();
	}

	@Override
	public boolean stopped() {
		return processor.stopped();
	}

	@Override
	public void check() {
		processor.check();
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. The processor is already connected.
	}

	public static class MacroJsonSerializer extends StdSerializer<Alias> {

		public MacroJsonSerializer() {
			this(null);
		}

		public MacroJsonSerializer(Class<Alias> t) {
			super(t);
		}

		@Override
		public void serialize(Alias value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException {
			jgen.writeRaw(value.descriptor.getName());
			jgen.writeRaw("(");
			jgen.writeObject(value.descriptor.getDescriptor());
			jgen.writeRaw(")");
		}

	}

}
