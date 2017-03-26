package com.untrackr.alerter.processor.primitives.consumer.post;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;

public class PostConfig extends ActiveProcessorConfig {

	private String path;

	@ImplicitProperty
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
