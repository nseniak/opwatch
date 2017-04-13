package com.untrackr.alerter.channel.services.pushover;

import com.untrackr.alerter.channel.common.ServiceConfiguration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PushoverConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;
	private int maxPerMinute = 10;
	private int emergencyRetry = (int) TimeUnit.SECONDS.toSeconds(60);
	private int emergencyExpire = (int) TimeUnit.SECONDS.toSeconds(3600);

	public static class Application {

		private String name;
		private String apiToken;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getApiToken() {
			return apiToken;
		}

		public void setApiToken(String apiToken) {
			this.apiToken = apiToken;
		}

	}

	public static class User {

		private String name;
		private String userKey;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUserKey() {
			return userKey;
		}

		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}

	}

	public static class ChannelConfig {

		private String apiToken;
		private String userKey;

		public String getApiToken() {
			return apiToken;
		}

		public void setApiToken(String apiToken) {
			this.apiToken = apiToken;
		}

		public String getUserKey() {
			return userKey;
		}

		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}
	}

	public Map<String, ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

	public int getMaxPerMinute() {
		return maxPerMinute;
	}

	public void setMaxPerMinute(int maxPerMinute) {
		this.maxPerMinute = maxPerMinute;
	}

	public int getEmergencyRetry() {
		return emergencyRetry;
	}

	public void setEmergencyRetry(int emergencyRetry) {
		this.emergencyRetry = emergencyRetry;
	}

	public int getEmergencyExpire() {
		return emergencyExpire;
	}

	public void setEmergencyExpire(int emergencyExpire) {
		this.emergencyExpire = emergencyExpire;
	}

}
