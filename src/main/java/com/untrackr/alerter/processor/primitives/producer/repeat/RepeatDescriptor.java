package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptProducer;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;

public class RepeatDescriptor extends ScheduledProcessorDescriptor {

	private JavascriptProducer producer;

	@DefaultOption
	public JavascriptProducer getProducer() {
		return producer;
	}

	public void setProducer(JavascriptProducer producer) {
		this.producer = producer;
	}

}
