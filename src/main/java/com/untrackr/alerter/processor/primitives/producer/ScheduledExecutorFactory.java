package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledExecutorFactory<D extends ScheduledProcessorConfig, P extends ActiveProcessor> extends ActiveProcessorFactory<D, P> {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected ScheduledExecutor makeScheduledExecutor(ScheduledProcessorConfig descriptor) {
		long defaultPeriod = getProcessorService().getProfileService().profile().getDefaultScheduledProducerPeriod();
		long period = optionalDurationValue("period", descriptor.getPeriod(), defaultPeriod);
		return new ScheduledExecutor(processorService, period);
	}

}
