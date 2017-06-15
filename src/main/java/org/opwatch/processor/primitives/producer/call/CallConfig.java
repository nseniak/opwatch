package org.opwatch.processor.primitives.producer.call;

import org.opwatch.processor.config.*;

public class CallConfig extends ScheduledProcessorConfig {

	private JavascriptConsumer input;
	private JavascriptProducer output;

	@ImplicitProperty
	public JavascriptProducer getOutput() {
		return output;
	}

	public void setOutput(JavascriptProducer output) {
		this.output = output;
	}

	@OptionalProperty
	public JavascriptConsumer getInput() {
		return input;
	}

	public void setInput(JavascriptConsumer input) {
		this.input = input;
	}

}
