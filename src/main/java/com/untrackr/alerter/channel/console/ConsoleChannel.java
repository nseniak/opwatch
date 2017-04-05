package com.untrackr.alerter.channel.console;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.processor.common.Message;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_CHANNEL_NAME;

public class ConsoleChannel implements Channel {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleChannel.class);

	private ConsoleConfiguration config;
	private ConsoleMessageService service;
	private ProcessorService processorService;

	public ConsoleChannel(ConsoleConfiguration config, ConsoleMessageService service, ProcessorService processorService) {
		this.config = config;
		this.service = service;
		this.processorService = processorService;
	}

	@Override
	public String serviceName() {
		return service.serviceName();
	}

	@Override
	public String name() {
		return CONSOLE_CHANNEL_NAME;
	}

	@Override
	public void publish(Message message) {
		String logMessage = "Message to Console " + processorService.prettyJson(message);
		logger.info(logMessage);
		if (processorService.config().isChannelDebug()) {
			processorService.printStdout(logMessage);
		} else {
			processorService.printStdout(message.getType() + ": " + message.getTitle());
			if (message.getData() != null) {
				String stack = message.getData().get("stack");
				if (stack != null) {
					processorService.printStdout(stack);
				}
			}
		}
	}

}
