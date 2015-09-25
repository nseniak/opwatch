package com.untrackr.alerter.model.descriptor;

import com.untrackr.alerter.model.common.JsonObject;

import java.util.List;

public class PipeDesc extends ProcessorDesc {

	private List<JsonObject> pipe;

	public List<JsonObject> getPipe() {
		return pipe;
	}

	public void setPipe(List<JsonObject> pipe) {
		this.pipe = pipe;
	}

}
