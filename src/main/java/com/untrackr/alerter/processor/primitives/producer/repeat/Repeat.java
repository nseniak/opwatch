package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.descriptor.JavascriptProducer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Repeat extends ScheduledProducer<RepeatDescriptor> {

	private JavascriptProducer producer;

	public Repeat(ProcessorService processorService, RepeatDescriptor descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.producer = producer;
	}

	@Override
	protected void produce() {
		Object result = producer.call(this);
		if (result != null) {
			outputProduced(result);
		}
	}

}
