package com.untrackr.alerter.channel.common;

import java.util.Map;

public class Channels {

	private Map<String, Channel> channelMap;

	private Channel applicationChannel;
	private Channel systemChannel;
	private Channel defaultConsoleChannel;
	private Channel fallbackChannel;

	public Channels(Map<String, Channel> channelMap,
									Channel applicationChannel,
									Channel systemChannel,
									Channel fallbackChannel,
									Channel defaultConsoleChannel) {
		this.channelMap = channelMap;
		this.applicationChannel = applicationChannel;
		this.systemChannel = systemChannel;
		this.defaultConsoleChannel = defaultConsoleChannel;
		this.fallbackChannel = fallbackChannel;
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, Channel> channelMap) {
		this.channelMap = channelMap;
	}

	public Channel getApplicationChannel() {
		return applicationChannel;
	}

	public void setApplicationChannel(Channel applicationChannel) {
		this.applicationChannel = applicationChannel;
	}

	public Channel getSystemChannel() {
		return systemChannel;
	}

	public void setSystemChannel(Channel systemChannel) {
		this.systemChannel = systemChannel;
	}

	public Channel getDefaultConsoleChannel() {
		return defaultConsoleChannel;
	}

	public void setDefaultConsoleChannel(Channel defaultConsoleChannel) {
		this.defaultConsoleChannel = defaultConsoleChannel;
	}

	public Channel getFallbackChannel() {
		return fallbackChannel;
	}

	public void setFallbackChannel(Channel fallbackChannel) {
		this.fallbackChannel = fallbackChannel;
	}

}
