package org.opwatch.channel.services.slack;

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;

public class SlackConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;
	private int maxPerMinute = 10;

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

		private String webhookUrl;

		public String getWebhookUrl() {
			return webhookUrl;
		}

		public void setWebhookUrl(String webhookUrl) {
			this.webhookUrl = webhookUrl;
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

}
