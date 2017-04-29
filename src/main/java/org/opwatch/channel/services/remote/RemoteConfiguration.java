package org.opwatch.channel.services.remote;

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;

public class RemoteConfiguration extends ServiceConfiguration {

	private Map<String, RemoteConfiguration.ChannelConfig> channels;

	public static class ChannelConfig {

		private String hostname;
		private Integer port;
		private String channel;

		public String getHostname() {
			return hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

	}

	public Map<String, RemoteConfiguration.ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

}
