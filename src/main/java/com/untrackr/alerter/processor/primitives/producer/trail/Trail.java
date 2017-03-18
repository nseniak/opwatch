package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;
import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.payload.ObjectSeries;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Trail extends TrailCollecter<TrailDescriptor> {

	private JavascriptTransformer transformer;

	public Trail(ProcessorService processorService, TrailDescriptor descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptTransformer transformer, long duration) {
		super(processorService, descriptor, name, scheduledExecutor, duration);
		this.transformer = transformer;
	}

	@Override
	protected Object collectedObject(Payload payload) {
		return (transformer == null) ? payload.getValue() : transformer.call(payload, this);
	}

	@Override
	protected Object producedObject() {
		return new ObjectSeries(queue);
	}

}
