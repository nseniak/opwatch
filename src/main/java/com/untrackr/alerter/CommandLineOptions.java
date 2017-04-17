package com.untrackr.alerter;

import java.util.List;

public class CommandLineOptions {

	private String hostname;
	private boolean noServer;
	private String initScript;
	private boolean noInit;
	private Integer port;
	private boolean traceChannels;
	private String runExpression;
	private List<String> scripts;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isNoServer() {
		return noServer;
	}

	public void setNoServer(boolean noServer) {
		this.noServer = noServer;
	}

	public String getInitScript() {
		return initScript;
	}

	public void setInitScript(String initScript) {
		this.initScript = initScript;
	}

	public boolean isNoInit() {
		return noInit;
	}

	public void setNoInit(boolean noInit) {
		this.noInit = noInit;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isTraceChannels() {
		return traceChannels;
	}

	public void setTraceChannels(boolean traceChannels) {
		this.traceChannels = traceChannels;
	}

	public String getRunExpression() {
		return runExpression;
	}

	public void setRunExpression(String runExpression) {
		this.runExpression = runExpression;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

}
