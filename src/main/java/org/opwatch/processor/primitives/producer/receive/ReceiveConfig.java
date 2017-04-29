package org.opwatch.processor.primitives.producer.receive;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;

public class ReceiveConfig extends ActiveProcessorConfig {

	private String path;

	@ImplicitProperty
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
