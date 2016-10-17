package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Count extends ScheduledProducer {

	private int count = 0;

	public Count(ProcessorService processorService, ScriptStack stack, ScheduledExecutor scheduledExecutor) {
		super(processorService, stack, scheduledExecutor);
	}

	@Override
	protected void produce() {
		outputProduced(count++);
	}

}
