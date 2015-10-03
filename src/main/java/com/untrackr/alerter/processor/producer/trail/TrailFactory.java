package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.CompiledScript;

public class TrailFactory extends ScheduledExecutorFactory {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "trail";
	}

	@Override
	public Trail make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		TrailDesc descriptor = convertDescriptor(path, TrailDesc.class, jsonDescriptor);
		String valueSource = optionalFieldValue(path, jsonDescriptor, "value", descriptor.getValue(), null);
		CompiledScript valueScript = (valueSource == null) ? null : compileScript(path, jsonDescriptor, "value", valueSource);
		long duration = durationValue(path, jsonDescriptor, "duration", descriptor.getDuration());
		Trail trail = new Trail(getProcessorService(), path, makeScheduledExecutor(path, jsonDescriptor, descriptor), valueSource, valueScript, duration);
		initialize(trail, descriptor);
		return trail;
	}

}
