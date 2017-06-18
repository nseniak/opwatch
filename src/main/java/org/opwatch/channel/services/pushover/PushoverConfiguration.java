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

import org.opwatch.channel.common.ServiceConfiguration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PushoverConfiguration extends ServiceConfiguration {

	private Map<String, ChannelConfig> channels;

	public static class ChannelConfig {

		private String apiToken;
		private String userKey;
		private int emergencyRetry = (int) TimeUnit.SECONDS.toSeconds(60);
		private int emergencyExpire = (int) TimeUnit.SECONDS.toSeconds(3600);
		private int maxPerMinute = 10;

		public String getApiToken() {
			return apiToken;
		}

		public void setApiToken(String apiToken) {
			this.apiToken = apiToken;
		}

		public String getUserKey() {
			return userKey;
		}

		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}

		public int getEmergencyRetry() {
			return emergencyRetry;
		}

		public void setEmergencyRetry(int emergencyRetry) {
			this.emergencyRetry = emergencyRetry;
		}

		public int getEmergencyExpire() {
			return emergencyExpire;
		}

		public void setEmergencyExpire(int emergencyExpire) {
			this.emergencyExpire = emergencyExpire;
		}

		public int getMaxPerMinute() {
			return maxPerMinute;
		}

		public void setMaxPerMinute(int maxPerMinute) {
			this.maxPerMinute = maxPerMinute;
		}

	}

	public Map<String, ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(Map<String, ChannelConfig> channels) {
		this.channels = channels;
	}

}
