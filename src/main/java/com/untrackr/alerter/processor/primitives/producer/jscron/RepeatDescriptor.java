package com.untrackr.alerter.processor.primitives.producer.jscron;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptProducer;
import com.untrackr.alerter.processor.descriptor.ScheduledProducerDescriptor;

public class RepeatDescriptor extends ScheduledProducerDescriptor {

	private JavascriptProducer producer;

	@DefaultOption
	public JavascriptProducer getProducer() {
		return producer;
	}

	public void setProducer(JavascriptProducer producer) {
		this.producer = producer;
	}

}
