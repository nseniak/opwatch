package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.payload.Payload;

public class ConstantOrFilter<T> extends ConfigPropertyValue {

	public enum ProducerType {
		constant, functional
	}

	private ProducerType type;
	private T constant;
	private JavascriptFilter producer;

	private ConstantOrFilter() {
	}

	public static <T> ConstantOrFilter<T> makeConstant(T constant) {
		ConstantOrFilter<T> producer = new ConstantOrFilter();
		producer.type = ProducerType.constant;
		producer.constant = constant;
		return producer;
	}

	public static ConstantOrFilter makeFunctional(JavascriptFilter function, ValueLocation valueLocation) {
		ConstantOrFilter producer = new ConstantOrFilter();
		producer.type = ProducerType.functional;
		producer.producer = function;
		return producer;
	}

	public T value(Processor processor, Payload payload, Class<T> clazz) {
		if (type == ProducerType.constant) {
			return constant;
		} else {
			Object value = producer.call(payload, processor);
			return (T) processor.getProcessorService().getScriptService().convertScriptValue(producer.getValueLocation(), clazz, value,
					(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
		}
	}

	public T getConstant() {
		return constant;
	}

	public JavascriptFilter getProducer() {
		return producer;
	}

}
