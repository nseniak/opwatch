package com.untrackr.alerter.channel.common;

import java.util.Map;

public class Channels {

	private Map<String, Channel> channelMap;

	private Channel defaultChannel;

	private Channel errorChannel;

	public Channels(Map<String, Channel> channelMap, Channel defaultChannel, Channel errorChannel) {
		this.channelMap = channelMap;
		this.defaultChannel = defaultChannel;
		this.errorChannel = errorChannel;
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, Channel> channelMap) {
		this.channelMap = channelMap;
	}

	public Channel getDefaultChannel() {
		return defaultChannel;
	}

	public void setDefaultChannel(Channel defaultChannel) {
		this.defaultChannel = defaultChannel;
	}

	public Channel getErrorChannel() {
		return errorChannel;
	}

	public void setErrorChannel(Channel errorChannel) {
		this.errorChannel = errorChannel;
	}

}
