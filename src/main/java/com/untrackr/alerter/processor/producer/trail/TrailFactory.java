package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory<TrailDesc, Trail> {

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
		TrailDesc descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptTransformer transformer = descriptor.getTransformer();
		long duration = durationValue("duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), transformer, duration);
		return trail;
	}

}
