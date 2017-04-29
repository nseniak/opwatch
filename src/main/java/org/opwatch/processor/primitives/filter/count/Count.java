package org.opwatch.processor.primitives.filter.count;

import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.common.TrailCollecter;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.service.ProcessorService;

public class Count extends TrailCollecter<CountConfig> {

	private JavascriptPredicate predicate;

	public Count(ProcessorService processorService, CountConfig configuration, String name, ScheduledExecutor scheduledExecutor, JavascriptPredicate predicate, long duration) {
		super(processorService, configuration, name, scheduledExecutor, duration);
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
