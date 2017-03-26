package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.processor.common.CallbackErrorLocation;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValueLocation;
import com.untrackr.alerter.processor.payload.Payload;

public class StringValue extends ConfigPropertyValue {

	public enum ProducerType {
		constant, functional
	}

	private ProducerType type;
	private String constant;
	private JavascriptFilter producer;
	private ValueLocation valueLocation;

	private StringValue() {
	}

	public static StringValue makeConstant(String constant) {
		StringValue producer = new StringValue();
		producer.type = ProducerType.constant;
		producer.constant = constant;
		return producer;
	}

	public static StringValue makeFunctional(JavascriptFilter function, ValueLocation valueLocation) {
		StringValue producer = new StringValue();
		producer.type = ProducerType.functional;
		producer.producer = function;
		producer.valueLocation = valueLocation;
		return producer;
	}

	public String value(Processor processor, Payload payload) {
		if (type == ProducerType.constant) {
			return constant;
		} else {
			Object value = producer.call(payload, processor);
			return (String) processor.getProcessorService().getScriptService().convertScriptValue(producer.getValueLocation(), String.class, value,
					() -> ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation), payload));
		}
	}

	public String getConstant() {
		return constant;
	}

	public JavascriptFilter getProducer() {
		return producer;
	}

}
