package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.payload.Payload;

public class ConstantOrFilter<T> extends ConfigPropertyValue {

	public enum ValueType {
		constant, functional
	}

	private ValueType type;
	private T constant;
	private JavascriptFilter filter;

	private ConstantOrFilter() {
	}

	public static <T> ConstantOrFilter<T> makeConstant(T constant) {
		ConstantOrFilter<T> producer = new ConstantOrFilter();
		producer.type = ValueType.constant;
		producer.constant = constant;
		return producer;
	}

	public static ConstantOrFilter makeFunctional(JavascriptFilter function, ValueLocation valueLocation) {
		ConstantOrFilter producer = new ConstantOrFilter();
		producer.type = ValueType.functional;
		producer.filter = function;
		return producer;
	}

	public T value(Processor processor, Payload payload, Class<T> clazz) {
		if (type == ValueType.constant) {
			return constant;
		} else {
			Object value = filter.call(payload, processor);
			return (T) processor.getProcessorService().getScriptService().convertScriptValue(filter.getValueLocation(), clazz, value,
					(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
		}
	}

	public T getConstant() {
		return constant;
	}

	public JavascriptFilter getFilter() {
		return filter;
	}

}
