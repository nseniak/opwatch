package com.untrackr.alerter.channel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.untrackr.alerter.channel.common.*;
import com.untrackr.alerter.channel.console.ConsoleConfiguration;
import com.untrackr.alerter.channel.console.ConsoleMessageService;
import com.untrackr.alerter.channel.pushover.PushoverMessageService;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.service.CommandLineOptions;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_CHANNEL_NAME;
import static com.untrackr.alerter.channel.console.ConsoleMessageService.CONSOLE_SERVICE_NAME;

@Service
public class MessageServiceService {

	public Channels createChannels(CommandLineOptions options, ProcessorService processorService) {
		String serviceConfigFile = options.getServices();
		if (serviceConfigFile == null) {
			serviceConfigFile = "services.json";
		}
		Configuration config;
		try {
			config = processorService.getObjectMapper().readValue(new File(serviceConfigFile), Configuration.class);
		} catch (FileNotFoundException e) {
			throw new RuntimeError("file not found: " + serviceConfigFile);
		} catch (JsonMappingException | JsonParseException e) {
			throw new RuntimeError("cannot parse service configuration from file " + serviceConfigFile + ": " + e.getMessage());
		} catch (IOException e) {
			throw new RuntimeError("cannot read service configuration file " + serviceConfigFile + ": " + e.getMessage());
		}
		Map<String, Object> services = config.getServices();
		if (services == null) {
			services = new LinkedHashMap<>();
		}
		services.computeIfAbsent(CONSOLE_SERVICE_NAME, k -> new ConsoleConfiguration());
		LinkedHashMap<String, Channel> channelMap = new LinkedHashMap<>();
		addServiceChannels(services, channelMap, processorService, new ConsoleMessageService());
		Channel consoleChannel = channelMap.get(CONSOLE_CHANNEL_NAME);
		if (consoleChannel == null) {
			throw new IllegalStateException("console channel not defined");
		}
		addServiceChannels(services, channelMap, processorService, new PushoverMessageService());
		Channel defaultChannel = null;
		Channel errorChannel = null;
		String defaultChannelName = (options.getDefaultChannel() != null) ? options.getDefaultChannel() : config.getDefaultChannel();
		if (defaultChannelName != null) {
			defaultChannel = channelMap.get(defaultChannelName);
			if (defaultChannel == null) {
				throw new RuntimeError("the specified default channel does not exist: \"" + defaultChannelName + "\"");
			}
		} else {
			defaultChannel = consoleChannel;
		}
		String errorChannelName = (options.getErrorChannel() != null) ? options.getErrorChannel() : config.getErrorChannel();
		if (errorChannelName != null) {
			errorChannel = channelMap.get(errorChannelName);
			if (errorChannel == null) {
				throw new RuntimeError("the specified error channel does not exist: \"" + errorChannelName + "\"");
			}
		} else {
			errorChannel = defaultChannel;
		}
		return new Channels(channelMap, defaultChannel, errorChannel);
	}

	private <F extends ServiceConfiguration, C extends Channel> void addServiceChannels(Map<String, Object> services,
																																											LinkedHashMap<String, Channel> channelMap,
																																											ProcessorService processorService,
																																											MessageService<F, C> service) {
		String serviceName = service.serviceName();
		Object serviceConfig = services.get(serviceName);
		if (serviceConfig == null) {
			return;
		}
		Class<F> configClass = service.configurationClass();
		F config = processorService.getObjectMapper().convertValue(serviceConfig, configClass);
		List<C> channels = service.createChannels(config, processorService);
		for (C channel : channels) {
			String name = channel.name();
			Channel previous = channelMap.put(name, channel);
			if (previous != null) {
				throw new RuntimeError("cannot create " + channel.serviceName() + " channel \"" + name + "\": a channel with the same name already exists");
			}
		}
	}

}
