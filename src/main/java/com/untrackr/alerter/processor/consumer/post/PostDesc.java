package com.untrackr.alerter.processor.consumer.post;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class PostDesc extends ActiveProcessorDesc {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
