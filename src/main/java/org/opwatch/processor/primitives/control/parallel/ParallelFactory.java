package org.opwatch.processor.primitives.control.parallel;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.service.ProcessorService;

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
