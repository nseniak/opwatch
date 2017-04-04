package com.untrackr.alerter.channel.console;

import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.service.ProcessorService;

import java.util.Collections;
import java.util.List;

public class ConsoleMessageService implements MessageService<ConsoleConfiguration, ConsoleChannel> {

	public static final String CONSOLE_SERVICE_NAME = "console";
	public static final String CONSOLE_CHANNEL_NAME = "console";

	@Override
	public String serviceName() {
		return CONSOLE_CHANNEL_NAME;
	}

	@Override
	public Class<ConsoleConfiguration> configurationClass() {
		return ConsoleConfiguration.class;
	}

	@Override
	public List<ConsoleChannel> createChannels(ConsoleConfiguration config, ProcessorService processorService) {
		return Collections.singletonList(new ConsoleChannel(config, this, processorService));
	}

}
