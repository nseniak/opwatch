package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Trail extends TrailCollecter<TrailDesc> {

	private JavascriptTransformer transformer;

	public Trail(ProcessorService processorService, TrailDesc descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptTransformer transformer, long duration) {
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
