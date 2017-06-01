package org.opwatch.processor.config;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;

public class ValueOrFilter<T> extends ConfigPropertyValue {

	public enum Type {
		value, filter
	}

	private Type type;
	private T value;
	private JavascriptFilter filter;

	private ValueOrFilter() {
	}

	public static <T> ValueOrFilter<T> makeValue(T value) {
		ValueOrFilter<T> vof = new ValueOrFilter();
		vof.type = Type.value;
		vof.value = value;
		return vof;
	}

	public static <T> ValueOrFilter<T> makeFunction(JavascriptFilter function) {
		ValueOrFilter<T> vof = new ValueOrFilter<>();
		vof.type = Type.filter;
		vof.filter = function;
		return vof;
	}

	public T value(Processor processor, Payload payload, Class<T> clazz) {
		if (type == Type.value) {
			return value;
		} else {
			Object value = filter.call(payload, processor);
			return (T) processor.getProcessorService().getScriptService().convertScriptValue(filter.getValueLocation(), clazz, value,
					(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
		}
	}

	public T getValue() {
		return value;
	}

	public JavascriptFilter getFilter() {
		return filter;
	}

	public Type getType() {
		return type;
	}

}
