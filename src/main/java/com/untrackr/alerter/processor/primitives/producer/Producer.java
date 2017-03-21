package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.alert.Alert;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Producer<D extends ActiveProcessorDescriptor> extends ActiveProcessor<D> {

	public Producer(ProcessorService processorService, D descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void inferSignature() {
		this.signature = ProcessorSignature.makeProducer();
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		processorService.infrastructureAlert(Alert.Priority.high, "Producer should not receive input", getLocation().descriptor());
	}

}
