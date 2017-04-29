package org.opwatch.service;

public class HealthcheckInfo {

	private String hostname;
	private String runningProcessor;

	private HealthcheckInfo() {
	}

	public HealthcheckInfo(String hostname, String runningProcessor) {
		this.hostname = hostname;
		this.runningProcessor = runningProcessor;
	}

}
