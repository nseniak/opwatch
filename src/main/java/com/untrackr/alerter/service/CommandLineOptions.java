package com.untrackr.alerter.service;

import java.io.File;
import java.util.List;

public class CommandLineOptions {

	private String hostname;
	private String services;
	private String defaultChannel;
	private String errorChannel;
	private List<File> files;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
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

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
