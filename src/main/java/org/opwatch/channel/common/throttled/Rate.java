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
