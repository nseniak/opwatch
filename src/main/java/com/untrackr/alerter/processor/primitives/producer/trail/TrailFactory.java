package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory<TrailDescriptor, Trail> {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "trail";
	}

	@Override
	public Class<TrailDescriptor> descriptorClass() {
		return TrailDescriptor.class;
	}

	@Override
	public Trail make(Object scriptObject) {
		TrailDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptTransformer transformer = descriptor.getTransformer();
		long duration = durationValue("duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), transformer, duration);
		return trail;
	}

}
