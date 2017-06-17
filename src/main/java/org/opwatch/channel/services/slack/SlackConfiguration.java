package org.opwatch.channel.services.slack;

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;

public class SlackConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;

	public static class ChannelConfig {

		private String webhookUrl;
		private int maxPerMinute = 10;

		public String getWebhookUrl() {
			return webhookUrl;
		}

		public void setWebhookUrl(String webhookUrl) {
			this.webhookUrl = webhookUrl;
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
