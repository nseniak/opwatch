package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Trail extends TrailCollecter {

	private JavascriptTransformer transformer;

	public Trail(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor, JavascriptTransformer transformer, long duration) {
		super(processorService, name, scheduledExecutor, duration);
		this.transformer = transformer;
	}

	@Override
	protected Object collectedObject(Payload payload) {
		return (transformer == null) ? payload.getScriptObject() : transformer.call(payload, this);
	}

	@Override
	protected Object producedObject() {
		return new ObjectSeries(queue);
	}

}
