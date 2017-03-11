package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory<CountDesc, Count> {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "count";
	}

	@Override
	public Class<CountDesc> descriptorClass() {
		return CountDesc.class;
	}

	@Override
	public Count make(Object scriptObject) {
		CountDesc descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptPredicate predicate = optionaPropertyValue("predicate", descriptor.getPredicate(), null);
		long duration = durationValue("duration", descriptor.getDuration());
		Count count = new Count(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), predicate, duration);
		return count;
	}

}
