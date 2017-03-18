package com.untrackr.alerter.processor.primitives.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;

import java.util.List;

public class ParallelDescriptor extends ProcessorDescriptor {

	private List<Processor> processors;

	public List<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

}
