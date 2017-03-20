package com.untrackr.alerter.processor.primitives.transformer.once;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class OnceFactory extends ActiveProcessorFactory<OnceDescriptor, Once> {

	public OnceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "once";
	}

	@Override
	public Class<OnceDescriptor> descriptorClass() {
		return OnceDescriptor.class;
	}

	@Override
	public Once make(Object scriptObject) {
		OnceDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		long delay = durationValue("delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), descriptor, name(), delay);
		return once;
	}

}
