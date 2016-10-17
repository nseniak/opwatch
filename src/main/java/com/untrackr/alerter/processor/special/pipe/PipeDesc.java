package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorDesc;

import java.util.List;

public class PipeDesc extends ProcessorDesc {

	private List<Processor> processors;

	public List<Processor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor> processors) {
		this.processors = processors;
	}

}
