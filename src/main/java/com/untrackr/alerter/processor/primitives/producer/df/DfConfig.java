package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class DfConfig extends ScheduledProcessorConfig {

	private String file;

	@ImplicitProperty
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
