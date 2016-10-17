package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorDesc;

import java.util.List;

public class ParallelDesc extends ProcessorDesc {

	private List<Processor> processors;

	public List<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

}
