package com.untrackr.alerter.processor.common;

public class StringValue {

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
			return (String) payload.getProcessorService().getScriptService().convertScriptValue(function.getValueLocation(), String.class, value,
					() -> ExceptionContext.makeProcessorPayloadScriptCallback(processor, new CallbackErrorLocation(valueLocation), payload));
		}
	}

}
