package com.untrackr.alerter.service;

import java.io.File;
import java.util.List;

public class CommandLineOptions {

	private String hostname;
	private String startup;
	private List<File> files;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getStartup() {
		return startup;
	}

	public void setStartup(String startup) {
		this.startup = startup;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}

}
