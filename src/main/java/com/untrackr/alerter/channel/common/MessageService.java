package com.untrackr.alerter.channel.common;

import com.untrackr.alerter.service.ProcessorService;

import java.util.List;

public interface MessageService<S extends ServiceConfiguration, C extends Channel> {

	String serviceName();

	Class<S> configurationClass();

	List<C> createChannels(S config, ProcessorService processorService);

}
