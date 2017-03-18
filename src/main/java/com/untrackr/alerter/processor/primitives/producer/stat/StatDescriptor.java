package com.untrackr.alerter.processor.primitives.producer.stat;

import com.untrackr.alerter.processor.descriptor.ScheduledProducerDescriptor;

public class StatDescriptor extends ScheduledProducerDescriptor {

	private String file;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
