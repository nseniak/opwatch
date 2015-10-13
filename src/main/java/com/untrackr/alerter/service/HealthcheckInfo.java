package com.untrackr.alerter.service;

public class HealthcheckInfo {

	private String hostname;
	private String main;
	private boolean running;

	private HealthcheckInfo() {
	}

	public HealthcheckInfo(String hostname, String main, boolean running) {
		this.hostname = hostname;
		this.main = main;
		this.running = running;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMain() {
		return main;
	}

	public void setMain(String main) {
		this.main = main;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
