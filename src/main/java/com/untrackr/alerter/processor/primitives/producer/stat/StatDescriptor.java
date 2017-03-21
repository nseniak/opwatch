package com.untrackr.alerter.processor.primitives.producer.stat;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;

public class StatDescriptor extends ScheduledProcessorDescriptor {

	private String file;

	@DefaultOption
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
