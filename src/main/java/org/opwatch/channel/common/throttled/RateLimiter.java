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

import org.opwatch.common.Assertion;
import org.opwatch.common.TimestampCountTrail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

	private List<Rate> limits;
	private RateLimiter parent;
	private TimestampCountTrail timestampCountTrail;

	public RateLimiter(long initialTimestamp, List<Rate> limits, RateLimiter parent) {
		this.limits = limits;
		this.parent = parent;
		this.limits = new ArrayList<>(limits);
		// Sort from longest period to shortest
		Collections.sort(this.limits, Comparator.comparingInt(Rate::getSeconds).reversed());
		int maxPeriod = this.limits.stream().map(Rate::getSeconds).max(Integer::compareTo).orElse(0);
		Assertion.assertExecutionState("rate limit period cannot be more than 1 hour",maxPeriod < TimeUnit.HOURS.toSeconds(1), maxPeriod);
		this.timestampCountTrail = new TimestampCountTrail(initialTimestamp, maxPeriod);
	}

	public void consume(long timestamp, int count) {
		timestampCountTrail.increment(timestamp, count);
		if (parent != null) {
			parent.consume(timestamp, count);
		}
	}

	public Rate exceededRate(long timestamp) {
		for (Rate rateLimit : limits) {
			int trailSum = timestampCountTrail.trailSum(timestamp, rateLimit.getSeconds());
			if (trailSum >= rateLimit.getCount()) {
				return rateLimit;
			}
		}
		if (parent != null) {
			return parent.exceededRate(timestamp);
		}
		return null;
	}

	public RateLimiter root() {
		if (parent == null) {
			return this;
		} else {
			return parent.root();
		}
	}

}
