package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class ReceiveDescriptor extends ActiveProcessorDescriptor {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
