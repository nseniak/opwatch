package com.untrackr.alerter.processor.primitives.producer.stat;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class StatConfig extends ScheduledProcessorConfig {

	private String file;

	@ImplicitProperty
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
