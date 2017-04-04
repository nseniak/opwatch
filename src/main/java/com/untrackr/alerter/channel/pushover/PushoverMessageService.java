package com.untrackr.alerter.channel.pushover;

import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
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
		for (PushoverConfiguration.ChannelConfig channelConfig : config.getChannels()) {
			String name = channelConfig.getName();
			channels.add(new PushoverChannel(name, config, this, processorService));
		}
		return channels;
	}

}
