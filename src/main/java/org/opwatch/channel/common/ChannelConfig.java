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

import java.util.LinkedHashMap;
import java.util.Map;

public class ChannelConfig {

	private Map<String, Object> services = new LinkedHashMap<>();
	private String applicationChannel;
	private String systemChannel;
	private String fallbackChannel;

	public Map<String, Object> getServices() {
		return services;
	}

	public void setServices(LinkedHashMap<String, Object> services) {
		this.services = services;
	}

	public String getApplicationChannel() {
		return applicationChannel;
	}

	public void setApplicationChannel(String applicationChannel) {
		this.applicationChannel = applicationChannel;
	}

	public String getSystemChannel() {
		return systemChannel;
	}

	public void setSystemChannel(String systemChannel) {
		this.systemChannel = systemChannel;
	}

	public String getFallbackChannel() {
		return fallbackChannel;
	}

	public void setFallbackChannel(String fallbackChannel) {
		this.fallbackChannel = fallbackChannel;
	}

}
