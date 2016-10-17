package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		CountDesc descriptor = convertScriptDescriptor(CountDesc.class, jsonDescriptor);
		Count count = new Count(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor));
		initialize(count, descriptor);
		return count;
	}

}
