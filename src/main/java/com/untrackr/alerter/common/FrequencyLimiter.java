package com.untrackr.alerter.common;

import com.google.common.collect.EvictingQueue;

import java.util.concurrent.TimeUnit;

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

	public String describeLimit(String singleItem, String pluralItem) {
		long seconds = period / TimeUnit.SECONDS.toMillis(1);
		long minutes = period / TimeUnit.MINUTES.toMillis(1);
		String period;
		if (minutes < 1) {
			if (seconds == 1) {
				period = "second";
			} else {
				period = seconds + " seconds";
			}
		} else {
			if (minutes == 1) {
				period = "minute";
			} else {
				period = minutes + " minutes";
			}
		}
		String item;
		if (maxPerPeriod == 1) {
			item = singleItem;
		} else {
			item = pluralItem;
		}
		return maxPerPeriod + " " + item + " per " + period;
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
