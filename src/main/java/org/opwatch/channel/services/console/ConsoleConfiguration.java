package org.opwatch.channel.services.console;

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;

public class ConsoleConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;

	public static class ChannelConfig {

		private boolean addTimestamp;

		public boolean isAddTimestamp() {
			return addTimestamp;
		}

		public void setAddTimestamp(boolean addTimestamp) {
			this.addTimestamp = addTimestamp;
		}

	}

	public Map<String, ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

}
