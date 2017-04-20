package com.untrackr.alerter.processor.primitives.control.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ProcessorConfig;

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
