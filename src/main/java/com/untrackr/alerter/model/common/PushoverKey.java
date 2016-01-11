package com.untrackr.alerter.model.common;

public class PushoverKey {

	private String apiToken;
	private String userKey;

	public PushoverKey(String apiToken, String userKey) {
		this.apiToken = apiToken;
		this.userKey = userKey;
	}

	public String getApiToken() {
		return apiToken;
	}

	public String getUserKey() {
		return userKey;
	}

}
