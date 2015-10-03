package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "count";
	}

	@Override
	public Count make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		CountDesc descriptor = convertDescriptor(path, CountDesc.class, jsonDescriptor);
		Count count = new Count(getProcessorService(), path, makeScheduledExecutor(path, jsonDescriptor, descriptor));
		initialize(count, descriptor);
		return count;
	}

}
