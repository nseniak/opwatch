package com.untrackr.alerter.model.descriptor;

import com.untrackr.alerter.model.common.JsonObject;

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
