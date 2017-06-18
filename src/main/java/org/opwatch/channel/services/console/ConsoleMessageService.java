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

package org.opwatch.channel.services.console;

import org.opwatch.channel.common.Channel;
import org.opwatch.channel.common.MessageService;
import org.opwatch.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ConsoleMessageService implements MessageService<ConsoleConfiguration> {

	public static final String CONSOLE_SERVICE_NAME = "console";
	public static final String DEFAULT_CONSOLE_CHANNEL_NAME = "console";

	@Autowired
	private ProcessorService processorService;

	private List<ConsoleChannel> channels;

	@Override
	public String serviceName() {
		return CONSOLE_SERVICE_NAME;
	}

	@Override
	public Class<ConsoleConfiguration> configurationClass() {
		return ConsoleConfiguration.class;
	}

	@Override
	public void createChannels(ConsoleConfiguration config) {
		channels = new ArrayList<>();
		if (config.getChannels() == null) {
			return;
		}
		for (String channelName : config.getChannels().keySet()) {
			ConsoleChannel channel = new ConsoleChannel(channelName, config, this, processorService);
			channels.add(channel);
		}
	}

	public ConsoleChannel makeDefaultChannel() {
		ConsoleConfiguration config = new ConsoleConfiguration();
		config.setChannels(new LinkedHashMap<>());
		config.getChannels().put(DEFAULT_CONSOLE_CHANNEL_NAME, new ConsoleConfiguration.ChannelConfig());
		return new ConsoleChannel(DEFAULT_CONSOLE_CHANNEL_NAME, config, this, processorService);
	}

	@Override
	public List<Channel> channels() {
		return new ArrayList<>(channels);
	}

}
