package com.untrackr.alerter.channel.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelConfig {

	private Map<String, Object> services = new LinkedHashMap<>();
	private String alertChannel;
	private String systemChannel;

	public Map<String, Object> getServices() {
		return services;
	}

	public void setServices(LinkedHashMap<String, Object> services) {
		this.services = services;
	}

	public String getAlertChannel() {
		return alertChannel;
	}

	public void setAlertChannel(String alertChannel) {
		this.alertChannel = alertChannel;
	}

	public String getSystemChannel() {
		return systemChannel;
	}

	public void setSystemChannel(String systemChannel) {
		this.systemChannel = systemChannel;
	}

}
