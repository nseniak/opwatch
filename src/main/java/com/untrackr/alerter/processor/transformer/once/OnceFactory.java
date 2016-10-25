package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class OnceFactory extends ActiveProcessorFactory {

	public OnceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "once";
	}

	@Override
	public Once make(Object scriptObject) {
		OnceDesc descriptor = convertProcessorArgument(OnceDesc.class, scriptObject);
		long delay = durationValue("delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), displayName(descriptor), delay);
		return once;
	}

}
