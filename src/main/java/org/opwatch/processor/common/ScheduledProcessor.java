package org.opwatch.processor.common;

import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.service.ProcessorService;

public abstract class ScheduledProcessor<D extends ScheduledProcessorConfig> extends ActiveProcessor<D> {

	private ScheduledExecutor scheduledExecutor;

	public ScheduledProcessor(ProcessorService processorService, D configuration, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, configuration, name);
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
