package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.ScheduledProducerDescriptor;

public class DfDescriptor extends ScheduledProducerDescriptor {

	private String file;

	@DefaultOption
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
