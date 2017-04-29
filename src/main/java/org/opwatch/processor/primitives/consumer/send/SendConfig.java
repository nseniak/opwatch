package org.opwatch.processor.primitives.consumer.send;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.OptionalProperty;

public class SendConfig extends ActiveProcessorConfig {

	private String hostname;
	private Integer port;
	private String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@OptionalProperty
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
