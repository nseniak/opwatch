package com.untrackr.alerter.processor.special.pipe;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ProcessorDesc;

import java.util.List;

public class PipeDesc extends ProcessorDesc {

	private List<JsonDescriptor> pipe;

	public List<JsonDescriptor> getPipe() {
		return pipe;
	}

	public void setPipe(List<JsonDescriptor> pipe) {
		this.pipe = pipe;
	}

}
