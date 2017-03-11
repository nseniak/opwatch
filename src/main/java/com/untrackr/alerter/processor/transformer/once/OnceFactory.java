package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class OnceFactory extends ActiveProcessorFactory<OnceDesc, Once> {

	public OnceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "once";
	}

	@Override
	public Class<OnceDesc> descriptorClass() {
		return OnceDesc.class;
	}

	@Override
	public Once make(Object scriptObject) {
		OnceDesc descriptor = convertProcessorDescriptor(scriptObject);
		long delay = durationValue("delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), descriptor, type(), delay);
		return once;
	}

}
