/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.channel.services.pushover;

import org.opwatch.channel.common.throttled.ThrottledChannel;
import org.opwatch.channel.common.throttled.ThrottledMessageService;
import org.opwatch.channel.common.throttled.Rate;
import org.opwatch.channel.common.throttled.RateLimiter;
import org.opwatch.service.ProcessorService;
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
