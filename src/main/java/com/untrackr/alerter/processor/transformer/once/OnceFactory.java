package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object scriptObject) throws ValidationError {
		OnceDesc descriptor = convertProcessorArgument(OnceDesc.class, scriptObject);
		long delay = durationValue("delay", descriptor.getDelay());
		Once once = new Once(getProcessorService(), ScriptStack.currentStack(), delay);
		initialize(once, descriptor);
		return once;
	}

}
