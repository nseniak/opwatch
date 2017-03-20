package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

public class ReceiveDescriptor extends ActiveProcessorDescriptor {

	private String path;

	@DefaultOption
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
