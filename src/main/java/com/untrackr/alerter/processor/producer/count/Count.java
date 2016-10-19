package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Count extends ScheduledProducer {

	private int count = 0;

	public Count(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, name, scheduledExecutor);
	}

	@Override
	protected void produce() {
		outputProduced(count++);
	}

}
