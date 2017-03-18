package com.untrackr.alerter.processor.primitives.consumer.post;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class PostDescriptor extends ActiveProcessorDescriptor {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
