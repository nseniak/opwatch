package org.opwatch.processor.primitives.producer.repeat;

import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.config.ScheduledProcessorConfig;

public class RepeatConfig extends ScheduledProcessorConfig {

	private JavascriptProducer lambda;

	@ImplicitProperty
	public JavascriptProducer getLambda() {
		return lambda;
	}

	public void setLambda(JavascriptProducer lambda) {
		this.lambda = lambda;
	}

}
