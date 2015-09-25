package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.descriptor.ScheduledProducerDesc;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledExecutorFactory extends Factory {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	ScheduledExecutor makeScheduledExecutor(ScheduledProducerDesc descriptor) {
		long period = (descriptor.getPeriod() != null)
				? descriptor.getPeriod()
				: getProcessorService().getProfileService().profile().getDefaultScheduledProducerPeriod();
		return new ScheduledExecutor(processorService, period);
	}

}
