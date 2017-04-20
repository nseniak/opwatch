package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.JavascriptProducer;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

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
