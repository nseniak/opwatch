package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.OptionalProperty;

public class TailConfig extends ActiveProcessorConfig {

	private String file;
	private Boolean ignoreBlank = false;

	@ImplicitProperty
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@OptionalProperty
	public Boolean getIgnoreBlank() {
		return ignoreBlank;
	}

	public void setIgnoreBlank(Boolean ignoreBlank) {
		this.ignoreBlank = ignoreBlank;
	}

}
