package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.ScheduledProducerDesc;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledExecutorFactory extends ActiveProcessorFactory {

	public ScheduledExecutorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected ScheduledExecutor makeScheduledExecutor(ScheduledProducerDesc descriptor) {
		long period = (descriptor.getPeriod() != null)
				? descriptor.getPeriod()
				: getProcessorService().getProfileService().profile().getDefaultScheduledProducerPeriod();
		return new ScheduledExecutor(processorService, period);
	}

}
