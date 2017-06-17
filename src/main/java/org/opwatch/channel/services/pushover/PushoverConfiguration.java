package org.opwatch.channel.services.pushover;

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PushoverConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;

	public static class ChannelConfig {

		private String apiToken;
		private String userKey;
		private int emergencyRetry = (int) TimeUnit.SECONDS.toSeconds(60);
		private int emergencyExpire = (int) TimeUnit.SECONDS.toSeconds(3600);
		private int maxPerMinute = 10;

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

		public int getMaxPerMinute() {
			return maxPerMinute;
		}

		public void setMaxPerMinute(int maxPerMinute) {
			this.maxPerMinute = maxPerMinute;
		}

	}

	public Map<String, ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

}
