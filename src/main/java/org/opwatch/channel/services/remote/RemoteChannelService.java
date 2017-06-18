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

package org.opwatch.channel.services.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opwatch.channel.common.Channel;
import org.opwatch.channel.common.MessageService;
import org.opwatch.processor.common.Message;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RestController
public class RemoteChannelService implements MessageService<RemoteConfiguration> {

	private static final Logger logger = LoggerFactory.getLogger(RemoteChannelService.class);

	@Autowired
	private ProcessorService processorService;

	private List<Channel> remoteChannels = new ArrayList<>();

	@Override
	public String serviceName() {
		return "remote";
	}

	@Override
	public Class<RemoteConfiguration> configurationClass() {
		return RemoteConfiguration.class;
	}

	@Override
	public void createChannels(RemoteConfiguration config) {
		remoteChannels = new ArrayList<>();
		if (config.getChannels() == null) {
			return;
		}
		for (String channelName : config.getChannels().keySet()) {
			RemoteChannel channel = new RemoteChannel(channelName, config, this, processorService);
			remoteChannels.add(channel);
		}
	}

	@Override
	public List<Channel> channels() {
		return remoteChannels;
	}

	@RequestMapping(value = "/publish/{channelName}", method = RequestMethod.POST)
	public ResponseEntity<String> put(HttpServletRequest request,
																	@PathVariable String channelName,
																	@RequestBody Message message) throws JsonProcessingException {
		Channel channel = processorService.getMessagingService().findChannel(channelName);
		if (channel == null) {
			return new ResponseEntity<String>("channel not found: \"" + channelName + "\"", HttpStatus.BAD_REQUEST);
		}
		try {
			processorService.publish(channel, message);
		} catch (Throwable t) {
			logger.error("Error while processing remote message", t);
			return new ResponseEntity<>(t.getMessage(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
