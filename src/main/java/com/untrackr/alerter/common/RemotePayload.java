package com.untrackr.alerter.common;

import com.untrackr.alerter.processor.common.Payload;

public class RemotePayload {

	private Object jsonObject;
	private long timestamp;
	private String hostname;

	private RemotePayload() {
	}

	public RemotePayload(Payload payload) {
		this.jsonObject = payload.getJsonObject();
		this.timestamp = payload.getTimestamp();
		this.hostname = payload.getHostname();
	}

	public Object getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(Object jsonObject) {
		this.jsonObject = jsonObject;
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
