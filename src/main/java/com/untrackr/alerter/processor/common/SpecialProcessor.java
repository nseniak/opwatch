package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ProcessorConfig;
import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public abstract class SpecialProcessor<C extends ProcessorConfig> extends Processor<C> {

	public SpecialProcessor(ProcessorService processorService, C configuration, String name) {
		super(processorService, configuration, name);
	}

	public void stop(List<Processor<?>> processors) {
		boolean ok = true;
		for (Processor<?> processor : processors) {
			ok = ok & processorService.withExceptionHandling("error stopping processor",
					() -> new ProcessorVoidExecutionScope(processor),
					processor::stop);
		}
		if (ok) {
			throw new RuntimeError("error stopping processor", new ProcessorVoidExecutionScope(this));
		}
	}

}
