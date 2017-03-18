package com.untrackr.alerter.common;

import com.google.common.collect.EvictingQueue;

public class FrequencyLimiter {

	private long period;
	private int maxPerPeriod;
	private EvictingQueue<Long> timestampQueue;
	private int overflowCount = 0;

	public FrequencyLimiter(long period, int maxPerPeriod) {
		this.period = period;
		this.maxPerPeriod = maxPerPeriod;
		this.timestampQueue = EvictingQueue.create(maxPerPeriod);
	}

	public int ping() {
		return ping(System.currentTimeMillis());
	}

	public int ping(long now) {
		if (overflow(now)) {
			overflowCount = overflowCount + 1;
		} else {
			overflowCount = 0;
		}
		timestampQueue.add(now);
		return overflowCount;
	}

	private boolean overflow(long now) {
		if (timestampQueue.size() < maxPerPeriod) {
			return false;
		}
		long elapsed = now - timestampQueue.peek();
		return (elapsed < period);
	}

	public long getPeriod() {
		return period;
	}

	public int getMaxPerPeriod() {
		return maxPerPeriod;
	}

	public int getOverflowCount() {
		return overflowCount;
	}

}
