package org.opwatch.channel.services.console;

import org.opwatch.channel.common.ChannelImpl;
import org.opwatch.processor.common.Message;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

public class ConsoleChannel extends ChannelImpl {

	private static final Logger logger = LoggerFactory.getLogger(ConsoleChannel.class);
	private static final String CONTENT_PREFIX = ">> ";

	private String name;
	private ConsoleConfiguration config;
	private ConsoleMessageService service;

	public ConsoleChannel(String name, ConsoleConfiguration config, ConsoleMessageService service, ProcessorService processorService) {
		super(processorService);
		this.name = name;
		this.config = config;
		this.service = service;
		if ((config.getChannels() == null) || (config.getChannels().get(name) == null)) {
			throw new RuntimeError("Console channel configuration not found: " + name);
		}
	}

	@Override
	public String serviceName() {
		return service.serviceName();
	}

	@Override
	public String name() {
		return name;
	}

	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	@Override
	public void publish(Message message) {
		String consolePrefix = logString() + " ";
		String contentPrefix = consolePrefix + CONTENT_PREFIX;
		String logMessage = consolePrefix + processorService.prettyJson(message);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout(logMessage);
		} else {
			processorService.printStdout(consolePrefix + displayTitle(message));
			if (config.getChannels().get(name).isAddTimestamp()) {
				processorService.printStdout(contentPrefix + "time: " + dateFormat.format(new Date(message.getTimestamp())));
			}
			String hostname = message.getContext().getHostname();
			if (!hostname.equals(processorService.hostName())) {
				processorService.printStdout(contentPrefix + "hostname: " + hostname);
			}
			String detailsString = detailsString(message);
			if (detailsString != null) {
				String detailsPrefix = (message.getType().isSystem()) ? "" : "details: ";
				String prefixedPretty = prefixLines(detailsPrefix + detailsString, contentPrefix);
				processorService.printStdout(prefixedPretty);
			}
		}
	}

	private String prefixLines(String text, String prefix) {
		String[] lines = text.split("\n");
		String[] prefixedLines = Stream.of(lines).map(s -> prefix + s).toArray(String[]::new);
		return String.join("\n", prefixedLines);
	}

	@Override
	public String logString() {
		return (name.equals(ConsoleMessageService.DEFAULT_CONSOLE_CHANNEL_NAME) ? "[" + serviceName() + "]" : "[" + serviceName() + " channel \"" + name() + "\"]");
	}

}
