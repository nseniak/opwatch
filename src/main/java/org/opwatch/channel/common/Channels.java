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

package org.opwatch.channel.common;

import java.util.Map;

public class Channels {

	private Map<String, Channel> channelMap;

	private Channel applicationChannel;
	private Channel systemChannel;
	private Channel defaultConsoleChannel;
	private Channel fallbackChannel;

	public Channels(Map<String, Channel> channelMap,
									Channel applicationChannel,
									Channel systemChannel,
									Channel fallbackChannel,
									Channel defaultConsoleChannel) {
		this.channelMap = channelMap;
		this.applicationChannel = applicationChannel;
		this.systemChannel = systemChannel;
		this.defaultConsoleChannel = defaultConsoleChannel;
		this.fallbackChannel = fallbackChannel;
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, Channel> channelMap) {
		this.channelMap = channelMap;
	}

	public Channel getApplicationChannel() {
		return applicationChannel;
	}

	public void setApplicationChannel(Channel applicationChannel) {
		this.applicationChannel = applicationChannel;
	}

	public Channel getSystemChannel() {
		return systemChannel;
	}

	public void setSystemChannel(Channel systemChannel) {
		this.systemChannel = systemChannel;
	}

	public Channel getDefaultConsoleChannel() {
		return defaultConsoleChannel;
	}

	public void setDefaultConsoleChannel(Channel defaultConsoleChannel) {
		this.defaultConsoleChannel = defaultConsoleChannel;
	}

	public Channel getFallbackChannel() {
		return fallbackChannel;
	}

	public void setFallbackChannel(Channel fallbackChannel) {
		this.fallbackChannel = fallbackChannel;
	}

}
