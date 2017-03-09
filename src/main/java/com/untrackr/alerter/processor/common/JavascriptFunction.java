package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jdk.nashorn.api.scripting.NashornException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.io.IOException;
import java.util.regex.Pattern;

@JsonSerialize(using = JavascriptFunction.JavascriptFunctionJsonSerializer.class)
public abstract class JavascriptFunction extends DescriptorFieldValue {

	protected ScriptObjectMirror function;
	protected ValueLocation valueLocation;

	protected JavascriptFunction(ScriptObjectMirror function, ValueLocation valueLocation) {
		this.function = function;
		this.valueLocation = valueLocation;
	}

	protected Object invoke(Processor processor, Payload payload) {
		try {
			return function.call(function, payload.getValue(), payload);
		} catch (NashornException e) {
			ExceptionContext context = ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation, e), payload);
			AlerterException exception = new AlerterException(e, context);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

	protected Object invoke(Processor processor) {
		try {
			return function.call(function);
		} catch (NashornException e) {
			ExceptionContext context = ExceptionContext.makeProcessorNoPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation, e));
			AlerterException exception = new AlerterException(e, context);
			exception.setSilent(processor.scriptErrorSignaled(this));
			throw exception;
		}
	}

	@Override
	public String toString() {
		return Pattern.compile("\n[\t ]*", Pattern.DOTALL).matcher(function.toString()).replaceAll(" ");
	}

	public ValueLocation getValueLocation() {
		return valueLocation;
	}

	public static class JavascriptFunctionJsonSerializer extends StdSerializer<JavascriptFunction> {

		public JavascriptFunctionJsonSerializer() {
			this(null);
		}

		public JavascriptFunctionJsonSerializer(Class<JavascriptFunction> t) {
			super(t);
		}

		@Override
		public void serialize(
				JavascriptFunction value, JsonGenerator jgen, SerializerProvider provider)
				throws IOException, JsonProcessingException {
//			jgen.writeRaw(value.function.toString());
			jgen.writeRawValue(value.function.toString());
		}
	}

}
