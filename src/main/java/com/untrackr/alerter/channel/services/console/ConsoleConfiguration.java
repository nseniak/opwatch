package com.untrackr.alerter.channel.services.console;

import com.untrackr.alerter.channel.common.ServiceConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.untrackr.alerter.channel.services.console.ConsoleMessageService.DEFAULT_CONSOLE_CHANNEL_NAME;

public class ConsoleConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;

	public static class ChannelConfig {
		// No options
	}

	public Map<String, ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

}
