package com.untrackr.alerter.processor.primitives.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class ParallelFactory extends ProcessorFactory<ParallelConfig, Parallel> {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "parallel";
	}

	@Override
	public Class<ParallelConfig> descriptorClass() {
		return ParallelConfig.class;
	}

	@Override
	public Parallel make(Object scriptObject) {
		ParallelConfig descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor<?>> processors = descriptor.getProcessors();
		return new Parallel(getProcessorService(), processors, descriptor, name());
	}

}
