package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.JavascriptProducer;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class RepeatConfig extends ScheduledProcessorConfig {

	private JavascriptProducer producer;

	@ImplicitProperty
	public JavascriptProducer getProducer() {
		return producer;
	}

	public void setProducer(JavascriptProducer producer) {
		this.producer = producer;
	}

}