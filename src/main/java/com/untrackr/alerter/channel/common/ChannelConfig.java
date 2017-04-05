package com.untrackr.alerter.channel.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelConfig {

	private Map<String, Object> services = new LinkedHashMap<>();
	private String defaultChannel;
	private String errorChannel;

	public Map<String, Object> getServices() {
		return services;
	}

	public void setServices(LinkedHashMap<String, Object> services) {
		this.services = services;
	}

	public String getDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(String defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public String getErrorChannel() {
		return errorChannel;
	}

	public void setErrorChannel(String errorChannel) {
		this.errorChannel = errorChannel;
	}

}
