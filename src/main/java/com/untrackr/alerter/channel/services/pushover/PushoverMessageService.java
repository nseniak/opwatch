package com.untrackr.alerter.channel.services.pushover;

import com.untrackr.alerter.channel.common.throttled.ThrottledChannel;
import com.untrackr.alerter.channel.common.throttled.ThrottledMessageService;
import com.untrackr.alerter.channel.common.throttled.Rate;
import com.untrackr.alerter.channel.common.throttled.RateLimiter;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PushoverMessageService extends ThrottledMessageService<PushoverConfiguration> {

	@Autowired
	private ProcessorService processorService;

	private Map<String, RateLimiter> apiTokenRateLimiter = new LinkedHashMap<>();

	@Override
	public String serviceName() {
		return "pushover";
	}

	@Override
	public Class<PushoverConfiguration> configurationClass() {
		return PushoverConfiguration.class;
	}

	@Override
	public List<ThrottledChannel<PushoverConfiguration>> doCreateChannels(PushoverConfiguration config) {
		List<ThrottledChannel<PushoverConfiguration>> channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return Collections.emptyList();
		}
		for (String channelName : config.getChannels().keySet()) {
			PushoverChannel channel = new PushoverChannel(channelName, config, this, processorService);
			channels.add(channel);
		}
		return channels;
	}

	public RateLimiter apiTokenRateLimit(String apiToken) {
		String apiToken_lc = apiToken.toLowerCase();
		RateLimiter rateLimiter = apiTokenRateLimiter.get(apiToken_lc);
		if (rateLimiter != null) {
			return rateLimiter;
		}
		return apiTokenRateLimiter.computeIfAbsent(apiToken_lc, s -> {
			Rate globalRate = new Rate((int) TimeUnit.SECONDS.toSeconds(1), 10);
			return new RateLimiter(timestampSeconds(), Arrays.asList(globalRate), null);
		});
	}

}
