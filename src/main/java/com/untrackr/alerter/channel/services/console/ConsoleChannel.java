package com.untrackr.alerter.channel.services.console;

import com.untrackr.alerter.channel.common.Channel;
import com.untrackr.alerter.processor.common.Message;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.untrackr.alerter.channel.services.console.ConsoleMessageService.DEFAULT_CONSOLE_CHANNEL_NAME;

public class ConsoleChannel implements Channel {

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
			processorService.printStdout(consolePrefix + message.getType() + ": " + message.getTitle());
			if (message.getBody() != null) {
				Object body = message.getBody();
				if (body != null) {
					if (!processorService.getScriptService().bean(body)) {
						for (String line : body.toString().split("\\R")) {
							processorService.printStdout(consolePrefix + CONTENT_PREFIX + line);
						}
					} else {
						processorService.getScriptService().mapFields(body, (key, value) -> {
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
		return (name.equals(DEFAULT_CONSOLE_CHANNEL_NAME) ? "[" + serviceName() + "]" : "[" + serviceName() + " channel \"" + name() + "\"]");
	}

}
