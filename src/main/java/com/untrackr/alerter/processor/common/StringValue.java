package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

@JsonSerialize(using = StringValue.StringValueJsonSerializer.class)
public class StringValue extends DescriptorFieldValue {

	public enum ProducerType {
		constant, functional
	}

	private ProducerType type;
	private String constant;
	private JavascriptTransformer function;
	private ValueLocation valueLocation;

	private StringValue() {
	}

	public static StringValue makeConstant(String constant) {
		StringValue producer = new StringValue();
		producer.type = ProducerType.constant;
		producer.constant = constant;
		return producer;
	}

	public static StringValue makeFunctional(JavascriptTransformer function, ValueLocation valueLocation) {
		StringValue producer = new StringValue();
		producer.type = ProducerType.functional;
		producer.function = function;
		producer.valueLocation = valueLocation;
		return producer;
	}

	public String value(Processor processor, Payload payload) {
		if (type == ProducerType.constant) {
			return constant;
		} else {
			Object value = function.call(payload, processor);
			return (String) processor.getProcessorService().getScriptService().convertScriptValue(function.getValueLocation(), String.class, value,
					() -> ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation), payload));
		}
	}

	public static class StringValueJsonSerializer extends StdSerializer<StringValue> {

		public StringValueJsonSerializer() {
			this(null);
		}

		public StringValueJsonSerializer(Class<StringValue> t) {
			super(t);
		}

		@Override
		public void serialize(StringValue value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
			switch (value.type) {
				case constant:
					jgen.writeString(value.constant);
					break;
				case functional:
					jgen.writeRawValue(value.function.toString());
					break;
			}
		}
	}

}
