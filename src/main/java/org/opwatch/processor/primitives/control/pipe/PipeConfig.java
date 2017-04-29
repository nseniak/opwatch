package org.opwatch.processor.primitives.control.pipe;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.ProcessorConfig;

import java.util.List;

public class PipeConfig extends ProcessorConfig {

	private List<Processor<?>> processors;

	@ImplicitProperty
	public List<Processor<?>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor<?>> processors) {
		this.processors = processors;
	}

}
