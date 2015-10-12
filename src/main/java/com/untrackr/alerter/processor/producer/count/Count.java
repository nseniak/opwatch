package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Count extends ScheduledProducer {

	private int count = 0;

	public Count(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor) {
		super(processorService, path, scheduledExecutor);
	}

	@Override
	protected void produce() {
		outputProduced(count++);
	}

}
