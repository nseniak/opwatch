package org.opwatch.processor.common;

import org.opwatch.processor.config.ProcessorConfig;
import org.opwatch.service.ProcessorService;

import java.util.List;

public abstract class ControlProcessor<C extends ProcessorConfig> extends Processor<C> {

	public ControlProcessor(ProcessorService processorService, C configuration, String name) {
		super(processorService, configuration, name);
	}

	public void stop(List<Processor<?>> processors) {
		boolean ok = true;
		for (Processor<?> processor : processors) {
			ok = ok & processorService.withExceptionHandling("error stopping processor",
					() -> new ProcessorVoidExecutionScope(processor),
					processor::stop);
		}
		if (!ok) {
			throw new RuntimeError("error stopping processor", new ProcessorVoidExecutionScope(this));
		}
	}

}
