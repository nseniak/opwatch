package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledProcessor<D extends ScheduledProcessorDescriptor> extends ActiveProcessor<D> {

	private ScheduledExecutor scheduledExecutor;

	public ScheduledProcessor(ProcessorService processorService, D descriptor, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, descriptor, name);
		this.scheduledExecutor = scheduledExecutor;
	}

	@Override
	public void start() {
		scheduledExecutor.schedule(() -> processorService.withProcessorErrorHandling(this, this::produce));
	}

	@Override
	public void stop() {
		scheduledExecutor.stop(this);
	}

	protected abstract void produce();

}
