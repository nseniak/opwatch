package com.untrackr.alerter.channel.services.console;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.channel.common.MessageService;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ConsoleMessageService implements MessageService<ConsoleConfiguration> {

	public static final String CONSOLE_SERVICE_NAME = "console";
	public static final String CONSOLE_CHANNEL_NAME = "console";

	@Autowired
	private ProcessorService processorService;

	private ConsoleChannel consoleChannel;

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
		consoleChannel = new ConsoleChannel(config, this, processorService);
	}

	@Override
	public List<Channel> channels() {
		return Collections.singletonList(consoleChannel);
	}

}
