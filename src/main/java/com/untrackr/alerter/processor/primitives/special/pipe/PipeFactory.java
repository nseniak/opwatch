package com.untrackr.alerter.processor.primitives.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class PipeFactory extends ProcessorFactory<PipeDescriptor, Pipe> {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "pipe";
	}

	@Override
	public Class<PipeDescriptor> descriptorClass() {
		return PipeDescriptor.class;
	}

	@Override
	public Pipe make(Object scriptObject) {
		PipeDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor> processors = descriptor.getProcessors();
		return new Pipe(getProcessorService(), processors, descriptor, type());
	}

}
