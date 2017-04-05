package com.untrackr.alerter.channel.pushover;

import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PushoverMessageService implements MessageService<PushoverConfiguration, PushoverChannel> {

	@Override
	public String serviceName() {
		return "pushover";
	}

	@Override
	public Class<PushoverConfiguration> configurationClass() {
		return PushoverConfiguration.class;
	}

	@Override
	public List<PushoverChannel> createChannels(PushoverConfiguration config, ProcessorService processorService) {
		List<PushoverChannel> channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return Collections.emptyList();
		}
		for (String channelName : config.getChannels().keySet()) {
			channels.add(new PushoverChannel(channelName, config, this, processorService));
		}
		return channels;
	}

}
