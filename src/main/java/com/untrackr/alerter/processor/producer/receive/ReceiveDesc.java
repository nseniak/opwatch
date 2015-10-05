package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class ReceiveDesc extends ActiveProcessorDesc {

	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
