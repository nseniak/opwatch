package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.TrailCollecter;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

public class Count extends TrailCollecter<CountDesc> {

	private JavascriptPredicate predicate;

	public Count(ProcessorService processorService, CountDesc descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptPredicate predicate, long duration) {
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
