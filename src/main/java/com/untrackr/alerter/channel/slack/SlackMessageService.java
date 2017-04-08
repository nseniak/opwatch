package com.untrackr.alerter.channel.slack;

import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SlackMessageService implements MessageService<SlackConfiguration, SlackChannel> {

	@Override
	public String serviceName() {
		return "slack";
	}

	@Override
	public Class<SlackConfiguration> configurationClass() {
		return SlackConfiguration.class;
	}

	@Override
	public List<SlackChannel> createChannels(SlackConfiguration config, ProcessorService processorService) {
		List<SlackChannel> channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return Collections.emptyList();
		}
		for (String channelName : config.getChannels().keySet()) {
			channels.add(new SlackChannel(channelName, config, this, processorService));
		}
		return channels;
	}

}
