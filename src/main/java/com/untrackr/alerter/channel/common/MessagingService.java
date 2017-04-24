package com.untrackr.alerter.channel.common;

import com.untrackr.alerter.channel.services.console.ConsoleConfiguration;
import com.untrackr.alerter.channel.services.console.ConsoleMessageService;
import com.untrackr.alerter.channel.services.pushover.PushoverMessageService;
import com.untrackr.alerter.channel.services.remote.RemoteChannelService;
import com.untrackr.alerter.channel.services.slack.SlackMessageService;
import com.untrackr.alerter.common.ObjectMapperService;
import com.untrackr.alerter.processor.common.RuntimeError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

import static com.untrackr.alerter.channel.services.console.ConsoleMessageService.DEFAULT_CONSOLE_CHANNEL_NAME;

@Service
public class MessagingService {

	private static final Logger logger = LoggerFactory.getLogger(MessagingService.class);

	@Autowired
	private ObjectMapperService objectMapperService;

	@Autowired
	private ConsoleMessageService consoleMessageService;

	@Autowired
	private PushoverMessageService pushoverMessageService;

	@Autowired
	private SlackMessageService slackMessageService;

	@Autowired
	private RemoteChannelService remoteChannelService;

	private Channels channels;

	public void initializeChannels(ChannelConfig channelConfig) {
		LinkedHashMap<String, Channel> channelMap = new LinkedHashMap<>();
		addServiceChannels(channelConfig, channelMap, consoleMessageService);
		addServiceChannels(channelConfig, channelMap, remoteChannelService);
		addServiceChannels(channelConfig, channelMap, pushoverMessageService);
		addServiceChannels(channelConfig, channelMap, slackMessageService);
		Channel defaultConsoleChannel = channelMap.computeIfAbsent(DEFAULT_CONSOLE_CHANNEL_NAME,
				k -> {
					consoleMessageService.createChannels(new ConsoleConfiguration());
					return consoleMessageService.makeDefaultChannel();
				});
		Channel applicationChannel = null;
		Channel systemChannel = null;
		Channel fallbackChannel = null;
		String applicationChannelName = channelConfig.getApplicationChannel();
		if (applicationChannelName != null) {
			applicationChannel = channelMap.get(applicationChannelName);
			if (applicationChannel == null) {
				throw new RuntimeError("the specified default alert channel does not exist: \"" + applicationChannelName + "\"");
			}
		} else {
			applicationChannel = defaultConsoleChannel;
		}
		String systemChannelName = channelConfig.getSystemChannel();
		if (systemChannelName != null) {
			systemChannel = channelMap.get(systemChannelName);
			if (systemChannel == null) {
				throw new RuntimeError("the specified system channel does not exist: \"" + systemChannelName + "\"");
			}
		} else {
			systemChannel = defaultConsoleChannel;
		}
		String fallbackChannelName = channelConfig.getFallbackChannel();
		if (fallbackChannelName != null) {
			fallbackChannel = channelMap.get(fallbackChannelName);
			if (fallbackChannel == null) {
				throw new RuntimeError("the specified fallback channel does not exist: \"" + systemChannelName + "\"");
			}
		} else {
			fallbackChannel = defaultConsoleChannel;
		}
		channels = new Channels(channelMap, applicationChannel, systemChannel, fallbackChannel, defaultConsoleChannel);
		logger.info("Setting alert channel: " + channels.getApplicationChannel().name());
		logger.info("Setting system channel: " + channels.getSystemChannel().name());
		logger.info("Setting fallback channel: " + channels.getFallbackChannel().name());
	}

	private <F extends ServiceConfiguration> void addServiceChannels(ChannelConfig channelConfig,
																																	 LinkedHashMap<String, Channel> channelMap,
																																	 MessageService<F> service) {
		String serviceName = service.serviceName();
		Object serviceConfig = channelConfig.getServices().get(serviceName);
		if (serviceConfig == null) {
			return;
		}
		Class<F> configClass = service.configurationClass();
		F config;
		try {
			config = objectMapperService.objectMapper().convertValue(serviceConfig, configClass);
		} catch (RuntimeException e) {
			String message = "invalid configuration for service \"" + serviceName + "\"";
			logger.error(message, e);
			throw new RuntimeError(message + ": " + e.getMessage());
		}
		service.createChannels(config);
		for (Channel channel : service.channels()) {
			String name = channel.name();
			Channel previous = channelMap.put(name, channel);
			if (previous != null) {
				throw new RuntimeError("cannot create " + channel.serviceName() + " channel \"" + name + "\": a channel with the same name already exists");
			}
		}
	}

	public Channel findChannel(String name) {
		return channels.getChannelMap().get(name);
	}

	public Channel defaultConsoleChannel() {
		return channels.getDefaultConsoleChannel();
	}

	public Channel applicationChannel() {
		return channels.getApplicationChannel();
	}

	public Channel systemChannel() {
		return channels.getSystemChannel();
	}

	public Channel fallbackChannel() { return channels.getFallbackChannel(); }

}
