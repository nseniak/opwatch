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

package org.opwatch.channel.services.slack;

import org.opwatch.channel.common.throttled.ThrottledChannel;
import org.opwatch.channel.common.throttled.ThrottledMessageService;
import org.opwatch.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SlackMessageService extends ThrottledMessageService<SlackConfiguration> {

	@Autowired
	private ProcessorService processorService;

	@Override
	public String serviceName() {
		return "slack";
	}

	@Override
	public Class<SlackConfiguration> configurationClass() {
		return SlackConfiguration.class;
	}

	@Override
	public List<ThrottledChannel<SlackConfiguration>> doCreateChannels(SlackConfiguration config) {
		List<ThrottledChannel<SlackConfiguration>> channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return Collections.emptyList();
		}
		for (String channelName : config.getChannels().keySet()) {
			SlackChannel channel = new SlackChannel(channelName, config, this, processorService);
			channels.add(channel);
		}
		return channels;
	}

}
