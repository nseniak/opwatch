package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.alert.Alert;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.common.ScheduledProcessor;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledProducer<D extends ScheduledProcessorConfig> extends ScheduledProcessor<D> {

	public ScheduledProducer(ProcessorService processorService, D descriptor, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, descriptor, name, scheduledExecutor);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		processorService.infrastructureAlert(Alert.Priority.high, "Producer should not receive input", getLocation().descriptor());
	}

}
