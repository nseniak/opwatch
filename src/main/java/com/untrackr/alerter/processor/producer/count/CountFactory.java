package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "count";
	}

	@Override
	public Processor make(Object scriptObject) {
		CountDesc descriptor = convertProcessorArgument(CountDesc.class, scriptObject);
		JavascriptPredicate predicate = optionaPropertyValue("predicate", descriptor.getPredicate(), null);
		long duration = durationValue("duration", descriptor.getDuration());
		Count count = new Count(getProcessorService(), displayName(descriptor), makeScheduledExecutor(descriptor), predicate, duration);
		return count;
	}

}
