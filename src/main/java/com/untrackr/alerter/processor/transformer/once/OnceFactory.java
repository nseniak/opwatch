package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorDesc;
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
	public Class<OnceDesc> descriptorClass() {
		return OnceDesc.class;
	}

	@Override
	public Once make(Object scriptObject) {
		OnceDesc descriptor = convertProcessorDescriptor(OnceDesc.class, scriptObject);
		long delay = durationValue("delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), descriptor, type(), delay);
		return once;
	}

}
