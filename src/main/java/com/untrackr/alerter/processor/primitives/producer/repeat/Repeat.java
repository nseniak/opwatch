package com.untrackr.alerter.processor.primitives.producer.repeat;

import com.untrackr.alerter.processor.config.JavascriptProducer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Repeat extends ScheduledProducer<RepeatConfig> {

	private JavascriptProducer producer;

	public Repeat(ProcessorService processorService, RepeatConfig configuration, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
		super(processorService, configuration, name, scheduledExecutor);
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
