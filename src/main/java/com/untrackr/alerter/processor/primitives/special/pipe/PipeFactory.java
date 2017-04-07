package com.untrackr.alerter.processor.primitives.special.pipe;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public class PipeFactory extends ProcessorFactory<PipeConfig, Pipe> {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "pipe";
	}

	@Override
	public Class<PipeConfig> configurationClass() {
		return PipeConfig.class;
	}

	@Override
	public Class<Pipe> processorClass() {
		return Pipe.class;
	}

	@Override
	public Pipe make(Object scriptObject) {
		PipeConfig config = convertProcessorConfig(scriptObject);
		List<Processor<?>> processors = config.getProcessors();
		if (processors.isEmpty()) {
			throw new RuntimeError("a pipe cannot be empty", new FactoryExecutionScope(this));
		}
		return new Pipe(getProcessorService(), processors, config, name());
	}

}
