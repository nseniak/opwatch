package com.untrackr.alerter.common;

import com.untrackr.alerter.processor.common.Payload;

public class RemotePayload {

	private Object scriptObject;
	private long timestamp;
	private String hostname;

	private RemotePayload() {
	}

	public RemotePayload(Payload payload) {
		this.scriptObject = payload.getValue();
		this.timestamp = payload.getTimestamp();
		this.hostname = payload.getHostname();
	}

	public Object getScriptObject() {
		return scriptObject;
	}

	public void setScriptObject(Object scriptObject) {
		this.scriptObject = scriptObject;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
