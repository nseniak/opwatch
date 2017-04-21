package com.untrackr.alerter.processor.primitives.control.parallel;

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
	public Class<ParallelConfig> configurationClass() {
		return ParallelConfig.class;
	}

	@Override
	public Class<Parallel> processorClass() {
		return Parallel.class;
	}

	@Override
	public Parallel make(Object scriptObject) {
		ParallelConfig config = convertProcessorConfig(scriptObject);
		List<Processor<?>> processors = config.getProcessors();
		return new Parallel(getProcessorService(), processors, config, name());
	}

}