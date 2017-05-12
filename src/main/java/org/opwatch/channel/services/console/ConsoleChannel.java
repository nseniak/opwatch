package org.opwatch.channel.services.console;

import org.opwatch.channel.common.ChannelImpl;
import org.opwatch.processor.common.Message;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleChannel extends ChannelImpl {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleChannel.class);
	private static final String CONTENT_PREFIX = ">> ";

	private String name;
	private ConsoleConfiguration config;
	private ConsoleMessageService service;
	private ProcessorService processorService;

	public ConsoleChannel(String name, ConsoleConfiguration config, ConsoleMessageService service, ProcessorService processorService) {
		this.name = name;
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
		return name;
	}

	@Override
	public void publish(Message message) {
		String consolePrefix = (name.equals("console") ? "[console]" : logString()) + " ";
		String logMessage = consolePrefix + processorService.prettyJson(message);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout(logMessage);
		} else {
			processorService.printStdout(consolePrefix + displayTitle(message));
			String hostname = message.getContext().getHostname();
			if (!hostname.equals(processorService.hostName())) {
				processorService.printStdout("Hostname: " + hostname);
			}
			if (message.getDetails() != null) {
				Object details = message.getDetails();
				if (details != null) {
					if (!processorService.getScriptService().bean(details)) {
						for (String line : details.toString().split("\\R")) {
							processorService.printStdout(consolePrefix + CONTENT_PREFIX + line);
						}
					} else {
						processorService.getScriptService().mapFields(details, (key, value) -> {
							if (value != null) {
								processorService.printStdout(consolePrefix + CONTENT_PREFIX + key + ": " + value.toString());
							}
						});
					}
				}
			}
		}
	}

	@Override
	public String logString() {
		return (name.equals(ConsoleMessageService.DEFAULT_CONSOLE_CHANNEL_NAME) ? "[" + serviceName() + "]" : "[" + serviceName() + " channel \"" + name() + "\"]");
	}

}
