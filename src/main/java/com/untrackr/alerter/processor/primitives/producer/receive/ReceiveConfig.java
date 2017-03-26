package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;

public class ReceiveConfig extends ActiveProcessorConfig {

	private String path;

	@ImplicitProperty
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
