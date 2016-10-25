package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class ParallelFactory extends ProcessorFactory {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "parallel";
	}

	@Override
	public Parallel make(Object scriptObject) {
		ParallelDesc descriptor = convertProcessorArgument(ParallelDesc.class, scriptObject);
		Parallel parallel = new Parallel(getProcessorService(), descriptor.getProcessors(), displayName(descriptor));
		parallel.inferSignature();
		return parallel;
	}

}
