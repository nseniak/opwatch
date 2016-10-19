package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledProducer extends Producer {

	private ScheduledExecutor scheduledExecutor;

	public ScheduledProducer(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, name);
		this.scheduledExecutor = scheduledExecutor;
	}

	@Override
	public void doStart() {
		scheduledExecutor.schedule(() -> processorService.withProcessorErrorHandling(this, this::produce));
	}

	@Override
	public void doStop() {
		scheduledExecutor.stop(this);
	}

	protected abstract void produce();

	@Override
	public void consume(Payload payload) {
		// Do nothing
	}

}
