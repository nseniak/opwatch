package com.untrackr.alerter.model.common;

public class PushoverKey {

	private String apiToken;
	private String userId;

	public PushoverKey(String apiToken, String userId) {
		this.apiToken = apiToken;
		this.userId = userId;
	}

	public String getApiToken() {
		return apiToken;
	}

	public String getUserId() {
		return userId;
	}

}
