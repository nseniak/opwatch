package com.untrackr.alerter.processor.primitives.producer.count;

import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory<CountDescriptor, Count> {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "count";
	}

	@Override
	public Class<CountDescriptor> descriptorClass() {
		return CountDescriptor.class;
	}

	@Override
	public Count make(Object scriptObject) {
		CountDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptPredicate predicate = descriptor.getPredicate();
		long duration = durationValue("duration", descriptor.getDuration());
		Count count = new Count(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), predicate, duration);
		return count;
	}

}
