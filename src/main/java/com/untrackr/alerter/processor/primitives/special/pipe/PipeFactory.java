package com.untrackr.alerter.processor.primitives.special.pipe;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class PipeFactory extends ProcessorFactory<PipeConfig, Pipe> {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "pipe";
	}

	@Override
	public Class<PipeConfig> configurationClass() {
		return PipeConfig.class;
	}

	@Override
	public Pipe make(Object scriptObject) {
		PipeConfig descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor<?>> processors = descriptor.getProcessors();
		if (processors.isEmpty()) {
			throw new AlerterException("a pipe cannot be empty", ExceptionContext.makeProcessorFactory(name()));
		}
		return new Pipe(getProcessorService(), processors, descriptor, name());
	}

}
