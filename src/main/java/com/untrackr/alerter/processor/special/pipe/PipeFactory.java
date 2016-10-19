package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.transformer.identity.Identity;
import com.untrackr.alerter.service.ProcessorService;

public class PipeFactory extends ProcessorFactory {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "pipe";
	}

	@Override
	public Processor make(Object scriptObject) {
		PipeDesc descriptor = convertProcessorArgument(PipeDesc.class, scriptObject);
		Processor processor;
		if (descriptor.getProcessors().isEmpty()) {
			processor = new Identity(getProcessorService(), displayName(descriptor));
		} else {
			processor = new Pipe(getProcessorService(), descriptor.getProcessors(), displayName(descriptor));
		}
		return processor;
	}

}
