package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.FactoryExecutionScope;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledExecutorFactory<D extends ScheduledProcessorConfig, P extends ActiveProcessor> extends ActiveProcessorFactory<D, P> {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected ScheduledExecutor makeScheduledExecutor(ScheduledProcessorConfig descriptor) {
		long period = checkPropertyValue("period", descriptor.getPeriod()).value(this);
		if (period <= 0) {
			throw new RuntimeError("duration must be strictly positive: " + descriptor.getPeriod(),
					new FactoryExecutionScope(this));
		}
		return new ScheduledExecutor(processorService, period);
	}

}
