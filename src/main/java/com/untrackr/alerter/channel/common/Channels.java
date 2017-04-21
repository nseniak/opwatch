package com.untrackr.alerter.channel.common;

import java.util.Map;

public class Channels {

	private Map<String, Channel> channelMap;

	private Channel alertChannel;
	private Channel systemChannel;
	private Channel defaultConsoleChannel;

	public Channels(Map<String, Channel> channelMap, Channel alertChannel, Channel systemChannel, Channel defaultConsoleChannel) {
		this.channelMap = channelMap;
		this.alertChannel = alertChannel;
		this.systemChannel = systemChannel;
		this.defaultConsoleChannel = defaultConsoleChannel;
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, Channel> channelMap) {
		this.channelMap = channelMap;
	}

	public Channel getAlertChannel() {
		return alertChannel;
	}

	public void setAlertChannel(Channel alertChannel) {
		this.alertChannel = alertChannel;
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

}
