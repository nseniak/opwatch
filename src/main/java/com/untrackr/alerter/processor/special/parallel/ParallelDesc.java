package com.untrackr.alerter.processor.special.parallel;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.ProcessorDesc;

import java.util.List;

public class ParallelDesc extends ProcessorDesc {

	private List<JsonObject> parallel;

	public List<JsonObject> getParallel() {
		return parallel;
	}

	public void setParallel(List<JsonObject> parallel) {
		this.parallel = parallel;
	}

}
