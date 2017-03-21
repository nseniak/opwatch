package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory<TrailDescriptor, Trail> {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trail";
	}

	@Override
	public Class<TrailDescriptor> descriptorClass() {
		return TrailDescriptor.class;
	}

	@Override
	public Trail make(Object scriptObject) {
		TrailDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		long duration = durationValue("duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), duration);
		return trail;
	}

}
