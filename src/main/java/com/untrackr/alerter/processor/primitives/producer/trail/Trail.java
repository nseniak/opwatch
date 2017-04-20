package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.payload.ObjectSeries;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Trail extends TrailCollecter<TrailConfig> {

	public Trail(ProcessorService processorService, TrailConfig configuration, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, configuration, name, scheduledExecutor, duration);
	}

	@Override
	protected Object collectedObject(Payload payload) {
		return payload.getValue();
	}

	@Override
	protected Object producedObject() {
		return new ObjectSeries(queue);
	}

}
