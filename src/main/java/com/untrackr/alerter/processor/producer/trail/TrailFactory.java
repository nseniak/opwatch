package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trail";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		TrailDesc descriptor = convertScriptDescriptor(TrailDesc.class, jsonDescriptor);
		JavascriptTransformer transformer = optionalFieldValue(jsonDescriptor, "transformer", descriptor.getTransformer(), null);
		long duration = durationValue(jsonDescriptor, "duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor), transformer, duration);
		initialize(trail, descriptor);
		return trail;
	}

}
