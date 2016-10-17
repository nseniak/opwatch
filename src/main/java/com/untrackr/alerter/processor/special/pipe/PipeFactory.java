package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.filter.identity.Identity;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		PipeDesc descriptor = convertScriptDescriptor(PipeDesc.class, jsonDescriptor);
		if (descriptor.getProcessors().isEmpty()) {
			return new Identity(getProcessorService(), ScriptStack.currentStack());
		}
		return new Pipe(getProcessorService(), descriptor.getProcessors(), ScriptStack.currentStack());
	}

}
