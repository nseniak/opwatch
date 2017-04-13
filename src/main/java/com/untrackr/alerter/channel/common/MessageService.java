package com.untrackr.alerter.channel.common;

import java.util.List;

public interface MessageService<S extends ServiceConfiguration> {

	String serviceName();

	Class<S> configurationClass();

	void createChannels(S config);

	List<Channel> channels();

}
