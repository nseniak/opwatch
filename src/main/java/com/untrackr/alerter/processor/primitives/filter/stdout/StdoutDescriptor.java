package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

public class StdoutDescriptor extends ActiveProcessorDescriptor {

	private Boolean payload;

	@DefaultOption
	public Boolean getPayload() {
		return payload;
	}

	public void setPayload(Boolean payload) {
		this.payload = payload;
	}

}
