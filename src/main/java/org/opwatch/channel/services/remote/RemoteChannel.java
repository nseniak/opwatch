package org.opwatch.channel.services.remote;

import org.opwatch.channel.common.Channel;
import org.opwatch.processor.common.GlobalExecutionScope;
import org.opwatch.processor.common.Message;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RemoteChannel implements Channel {

	private static final Logger logger = LoggerFactory.getLogger(RemoteChannel.class);

	String name;
	private RemoteConfiguration config;
	private RemoteChannelService service;
	private ProcessorService processorService;
	private String hostname;
	private int port;
	private String remoteChannel;
	private String path;
	private String url;
	private RestTemplate restTemplate = new RestTemplate();

	public RemoteChannel(String name, RemoteConfiguration config, RemoteChannelService service, ProcessorService processorService) {
		this.name = name;
		this.config = config;
		this.service = service;
		this.processorService = processorService;
		initializeChannel();
	}

	private void initializeChannel() {
		if ((config.getChannels() == null) || (config.getChannels().get(name) == null)) {
			throw new RuntimeError("Remote channel configuration not found: " + name);
		}
		RemoteConfiguration.ChannelConfig channelConfig = config.getChannels().get(name);
		hostname = channelConfig.getHostname();
		if (hostname == null) {
			throw new RuntimeError("Remote hostname not defined for channel \"" + name + "\"");
		}
		port = (channelConfig.getPort() != null) ? channelConfig.getPort() : processorService.config().defaultPostPort();
		remoteChannel = channelConfig.getChannel();
		if (remoteChannel == null) {
			throw new RuntimeError("Remote channel not defined for channel \"" + name + "\"");
		}
		path = "/publish/" + remoteChannel;
		url = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path(path).toUriString();
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
		String logMessage = logString() + " " + processorService.prettyJson(message);
		logger.info(logMessage);
		if (processorService.config().channelDebug()) {
			processorService.printStdout(logMessage);
		} else {
			processorService.postForEntityWithErrors(url, message, String.class, hostname, port, path,
					GlobalExecutionScope::new);
		}
	}

}
