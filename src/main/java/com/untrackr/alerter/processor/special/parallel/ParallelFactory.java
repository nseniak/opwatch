package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class ParallelFactory extends ProcessorFactory<ParallelDesc, Parallel> {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "parallel";
	}

	@Override
	public Class<ParallelDesc> descriptorClass() {
		return ParallelDesc.class;
	}

	@Override
	public Parallel make(Object scriptObject) {
		ParallelDesc descriptor = convertProcessorDescriptor(scriptObject);
		List<Processor> processors = descriptor.getProcessors();
		Parallel parallel = new Parallel(getProcessorService(), processors, descriptor, type());
		parallel.inferSignature();
		return parallel;
	}

}
