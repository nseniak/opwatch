package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.ProcessorDesc;

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
