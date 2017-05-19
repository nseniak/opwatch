package org.opwatch.service;

public class HealthcheckInfo {

	private String hostname;
	private boolean running;

	private HealthcheckInfo() {
	}

	public HealthcheckInfo(String hostname, boolean running) {
		this.hostname = hostname;
		this.running = running;
	}

	public String getHostname() {
		return hostname;
	}

	public boolean isRunning() {
		return running;
	}

}
