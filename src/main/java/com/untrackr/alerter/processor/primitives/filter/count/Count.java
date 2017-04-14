package com.untrackr.alerter.processor.primitives.filter.count;

import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Count extends TrailCollecter<CountConfig> {

	private JavascriptPredicate predicate;

	public Count(ProcessorService processorService, CountConfig descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptPredicate predicate, long duration) {
		super(processorService, descriptor, name, scheduledExecutor, duration);
		this.predicate = predicate;
	}

	@Override
	protected Object collectedObject(Payload payload) {
		if ((predicate == null) || predicate.call(payload, this)) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	protected Object producedObject() {
		return queue.size();
	}

}
