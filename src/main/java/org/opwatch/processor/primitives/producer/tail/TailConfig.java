package org.opwatch.processor.primitives.producer.tail;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.OptionalProperty;

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
