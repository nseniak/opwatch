package com.untrackr.alerter.channel.services.slack;

import com.untrackr.alerter.channel.common.gated.GatedChannel;
import com.untrackr.alerter.channel.common.gated.GatedMessageService;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SlackMessageService extends GatedMessageService<SlackConfiguration> {

	@Autowired
	private ProcessorService processorService;

	@Override
	public String serviceName() {
		return "slack";
	}

	@Override
	public Class<SlackConfiguration> configurationClass() {
		return SlackConfiguration.class;
	}

	@Override
	public List<GatedChannel<SlackConfiguration>> doCreateChannels(SlackConfiguration config) {
		List<GatedChannel<SlackConfiguration>> channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return Collections.emptyList();
		}
		for (String channelName : config.getChannels().keySet()) {
			SlackChannel channel = new SlackChannel(channelName, config, this, processorService);
			channels.add(channel);
		}
		return channels;
	}

}
