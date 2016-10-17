package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object scriptObject) throws ValidationError {
		ParallelDesc descriptor = convertProcessorArgument(ParallelDesc.class, scriptObject);
		Parallel parallel = new Parallel(getProcessorService(), descriptor.getProcessors(), ScriptStack.currentStack());
		parallel.inferSignature();
		return parallel;
	}

}
