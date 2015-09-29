package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class StatDesc extends ScheduledProducerDesc {

	private String file;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
