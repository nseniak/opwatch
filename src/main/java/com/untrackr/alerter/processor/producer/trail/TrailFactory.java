package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorDesc;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "trail";
	}

	@Override
	public Class<TrailDesc> descriptorClass() {
		return TrailDesc.class;
	}

	@Override
	public Trail make(Object scriptObject) {
		TrailDesc descriptor = convertProcessorDescriptor(TrailDesc.class, scriptObject);
		JavascriptTransformer transformer = optionaPropertyValue("transformer", descriptor.getTransformer(), null);
		long duration = durationValue("duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), transformer, duration);
		return trail;
	}

}
