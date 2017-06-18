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

package org.opwatch.channel.common.throttled;

import java.util.concurrent.TimeUnit;

public class Rate {

	private int seconds;
	private int count;

	public Rate(int seconds, int count) {
		this.seconds = seconds;
		this.count = count;
	}

	public String describe(String singleItem, String pluralItem) {
		long sec = seconds / TimeUnit.SECONDS.toSeconds(1);
		long min = seconds / TimeUnit.MINUTES.toSeconds(1);
		String period;
		if (min < 1) {
			if (sec == 1) {
				period = "second";
			} else {
				period = sec + " seconds";
			}
		} else {
			if (min == 1) {
				period = "minute";
			} else {
				period = min + " minutes";
			}
		}
		String item;
		if (count == 1) {
			item = singleItem;
		} else {
			item = pluralItem;
		}
		return count + " " + item + " per " + period;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getCount() {
		return count;
	}

}
