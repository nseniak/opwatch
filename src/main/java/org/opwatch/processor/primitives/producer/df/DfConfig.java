package org.opwatch.processor.primitives.producer.df;

import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.ScheduledProcessorConfig;

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
