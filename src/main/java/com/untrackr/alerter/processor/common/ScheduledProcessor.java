package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledProcessor<D extends ScheduledProcessorConfig> extends ActiveProcessor<D> {

	private ScheduledExecutor scheduledExecutor;

	public ScheduledProcessor(ProcessorService processorService, D descriptor, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, descriptor, name);
		this.scheduledExecutor = scheduledExecutor;
	}

	@Override
	public void start() {
		scheduledExecutor.schedule(() -> processorService.withExceptionHandling("error running scheduled processor",
				() -> new ProcessorVoidExecutionScope(this),
				this::produce));
	}

	@Override
	public void stop() {
		scheduledExecutor.stop(this);
	}

	protected abstract void produce();

}
