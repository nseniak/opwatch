package org.opwatch.channel.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelConfig {

	private Map<String, Object> services = new LinkedHashMap<>();
	private String applicationChannel;
	private String systemChannel;
	private String fallbackChannel;

	public Map<String, Object> getServices() {
		return services;
	}

	public void setServices(LinkedHashMap<String, Object> services) {
		this.services = services;
	}

	public String getApplicationChannel() {
		return applicationChannel;
	}

	public void setApplicationChannel(String applicationChannel) {
		this.applicationChannel = applicationChannel;
	}

	public String getSystemChannel() {
		return systemChannel;
	}

	public void setSystemChannel(String systemChannel) {
		this.systemChannel = systemChannel;
	}

	public String getFallbackChannel() {
		return fallbackChannel;
	}

	public void setFallbackChannel(String fallbackChannel) {
		this.fallbackChannel = fallbackChannel;
	}

}
