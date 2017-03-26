package com.untrackr.alerter.processor.primitives.producer.count;

import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory<CountConfig, Count> {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "count";
	}

	@Override
	public Class<CountConfig> descriptorClass() {
		return CountConfig.class;
	}

	@Override
	public Count make(Object scriptObject) {
		CountConfig descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptPredicate predicate = descriptor.getPredicate();
		long duration = durationValue("duration", descriptor.getDuration());
		Count count = new Count(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), predicate, duration);
		return count;
	}

}
