package com.untrackr.alerter.processor.primitives.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class ParallelFactory extends ProcessorFactory<ParallelDescriptor, Parallel> {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "parallel";
	}

	@Override
	public Class<ParallelDescriptor> descriptorClass() {
		return ParallelDescriptor.class;
	}

	@Override
	public Parallel make(Object scriptObject) {
		ParallelDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor<?>> processors = descriptor.getProcessors();
		return new Parallel(getProcessorService(), processors, descriptor, name());
	}

}
