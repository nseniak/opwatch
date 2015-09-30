package com.untrackr.alerter.processor.filter.once;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class OnceFactory extends ActiveProcessorFactory {

	public OnceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "once";
	}

	@Override
	public Once make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		OnceDesc descriptor = convertDescriptor(path, OnceDesc.class, jsonDescriptor);
		long delay = durationValue(path, jsonDescriptor, "delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), path, delay);
		initialize(once, descriptor);
		return once;
	}

}
