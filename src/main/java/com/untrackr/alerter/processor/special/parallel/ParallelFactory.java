package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.FactoryService;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParallelFactory extends ProcessorFactory {

	public ParallelFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "parallel";
	}

	@Override
	public Parallel make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError, IOException {
		ParallelDesc descriptor = convertDescriptor(path, ParallelDesc.class, jsonDescriptor);
		List<Processor> processors = new ArrayList<>();
		FactoryService factoryService = getProcessorService().getFactoryService();
		for (JsonDescriptor parallelDesc : descriptor.getParallel()) {
			processors.add(factoryService.makeProcessor(parallelDesc, path));
		}
		Parallel parallel = new Parallel(getProcessorService(), processors, path);
		parallel.inferSignature(jsonDescriptor, path);
		return parallel;
	}

}
