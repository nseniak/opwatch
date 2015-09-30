package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ProcessorDesc;

import java.util.List;

public class ParallelDesc extends ProcessorDesc {

	private List<JsonDescriptor> parallel;

	public List<JsonDescriptor> getParallel() {
		return parallel;
	}

	public void setParallel(List<JsonDescriptor> parallel) {
		this.parallel = parallel;
	}

}
