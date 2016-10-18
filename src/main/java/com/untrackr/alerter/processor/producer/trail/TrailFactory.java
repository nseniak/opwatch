package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
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
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		TrailDesc descriptor = convertProcessorArgument(TrailDesc.class, scriptObject);
		JavascriptTransformer transformer = optionalFieldValue("transformer", descriptor.getTransformer(), null);
		long duration = durationValue("duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(descriptor), transformer, duration);
		initialize(trail, descriptor);
		return trail;
	}

}
