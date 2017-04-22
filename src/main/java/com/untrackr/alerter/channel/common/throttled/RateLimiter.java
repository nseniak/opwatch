package com.untrackr.alerter.channel.common.throttled;

import com.untrackr.alerter.common.Assertion;
import com.untrackr.alerter.common.TimestampCountTrail;

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
