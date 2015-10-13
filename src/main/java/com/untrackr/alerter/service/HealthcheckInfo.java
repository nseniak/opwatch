package com.untrackr.alerter.service;

public class HealthcheckInfo {

	private String hostName;
	private String runningProcessor;
	private boolean errors;

	private HealthcheckInfo() {
	}

	public HealthcheckInfo(String hostName, String runningProcessor, boolean errors) {
		this.hostName = hostName;
		this.runningProcessor = runningProcessor;
		this.errors = errors;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getRunningProcessor() {
		return runningProcessor;
	}

	public void setRunningProcessor(String runningProcessor) {
		this.runningProcessor = runningProcessor;
	}

	public boolean isErrors() {
		return errors;
	}

	public void setErrors(boolean errors) {
		this.errors = errors;
	}

}
