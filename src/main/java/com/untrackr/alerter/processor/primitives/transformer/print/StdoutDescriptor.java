package com.untrackr.alerter.processor.primitives.transformer.print;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class StdoutDescriptor extends ActiveProcessorDescriptor {

	private Boolean payload;

	public Boolean getPayload() {
		return payload;
	}

	public void setPayload(Boolean payload) {
		this.payload = payload;
	}

}
