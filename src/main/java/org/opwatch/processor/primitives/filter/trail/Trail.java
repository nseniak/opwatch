package org.opwatch.processor.primitives.filter.trail;

import org.opwatch.processor.common.TrailCollecter;
import org.opwatch.processor.payload.ObjectSeries;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.SeriesObject;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.service.ProcessorService;

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
		SeriesObject[] objects = new SeriesObject[queue.size()];
		queue.toArray(objects);
		return ObjectSeries.toJavascript(processorService.getScriptService(), objects);
	}

}
