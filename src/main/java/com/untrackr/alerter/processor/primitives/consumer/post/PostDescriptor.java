package com.untrackr.alerter.processor.primitives.consumer.post;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

public class PostDescriptor extends ActiveProcessorDescriptor {

	private String path;

	@DefaultOption
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
