package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class PipeFactory extends ProcessorFactory<PipeDesc, Pipe> {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "pipe";
	}

	@Override
	public Class<PipeDesc> descriptorClass() {
		return PipeDesc.class;
	}

	@Override
	public Pipe make(Object scriptObject) {
		PipeDesc descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor> processors = descriptor.getProcessors();
		return new Pipe(getProcessorService(), processors, descriptor, type());
	}

}
