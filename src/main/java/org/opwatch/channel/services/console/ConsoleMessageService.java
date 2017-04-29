package org.opwatch.channel.services.console;

import org.opwatch.channel.common.Channel;
import org.opwatch.channel.common.MessageService;
import org.opwatch.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsoleMessageService implements MessageService<ConsoleConfiguration> {

	public static final String CONSOLE_SERVICE_NAME = "console";
	public static final String DEFAULT_CONSOLE_CHANNEL_NAME = "console";

	@Autowired
	private ProcessorService processorService;

	private List<ConsoleChannel> channels;

	@Override
	public String serviceName() {
		return CONSOLE_SERVICE_NAME;
	}

	@Override
	public Class<ConsoleConfiguration> configurationClass() {
		return ConsoleConfiguration.class;
	}

	@Override
	public void createChannels(ConsoleConfiguration config) {
		channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return;
		}
		for (String channelName : config.getChannels().keySet()) {
			ConsoleChannel channel = new ConsoleChannel(channelName, config, this, processorService);
			channels.add(channel);
		}
	}

	public ConsoleChannel makeDefaultChannel() {
		return new ConsoleChannel(DEFAULT_CONSOLE_CHANNEL_NAME, new ConsoleConfiguration(), this, processorService);
	}

	@Override
	public List<Channel> channels() {
		return new ArrayList<>(channels);
	}

}
