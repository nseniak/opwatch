package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory<TrailConfig, Trail> {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trail";
	}

	@Override
	public Class<TrailConfig> configurationClass() {
		return TrailConfig.class;
	}

	@Override
	public Trail make(Object scriptObject) {
		TrailConfig descriptor = convertProcessorDescriptor(scriptObject);
		long duration = durationValue(checkPropertyValue("duration", descriptor.getDuration()));
		Trail trail = new Trail(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), duration);
		return trail;
	}

}
