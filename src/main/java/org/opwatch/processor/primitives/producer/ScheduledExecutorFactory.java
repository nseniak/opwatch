package org.opwatch.processor.primitives.producer;

import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.service.ProcessorService;

public abstract class ScheduledExecutorFactory<D extends ScheduledProcessorConfig, P extends ActiveProcessor> extends ActiveProcessorFactory<D, P> {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected ScheduledExecutor makeScheduledExecutor(ScheduledProcessorConfig descriptor, boolean hasInitialDelay) {
		long period = checkPropertyValue("period", descriptor.getPeriod()).value(this);
		long delay = checkPropertyValue("delay", descriptor.getDelay()).value(this);
		if (period <= 0) {
			throw new RuntimeError("duration must be strictly positive: " + descriptor.getPeriod(),
					new FactoryExecutionScope(this));
		}
		if (delay < 0) {
			throw new RuntimeError("delay must be positive: " + descriptor.getDelay(),
					new FactoryExecutionScope(this));
		}
		return new ScheduledExecutor(processorService, delay, period);
	}

}
