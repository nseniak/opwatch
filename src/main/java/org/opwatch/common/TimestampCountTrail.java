package org.opwatch.common;

public class TimestampCountTrail {

	/**
	 * Count for each timestamp in the trail. trail[0] corresponds to the count for latestIndex, trail[1]
	 * corresponds to the count for latestIndex - 1, etc.
	 */
	private int[] trail;
	/**
	 * Timestamp corresponding to the latest element of the trail, i.e. trail[0] .
	 */
	private long latestTimestamp;
	/**
	 * Size of the trail prefix that contains valid counts. Is <= trail.length.
	 */
	private int initializedPrefixSize;

	public TimestampCountTrail(long initialTimestamp, int trailSize) {
		this.trail = new int[trailSize];
		this.initializedPrefixSize = 1;
		this.latestTimestamp = initialTimestamp;
	}

	/**
	 * Increment index's count.
	 *
	 * @param timestamp
	 * @param count
	 */
	public synchronized void increment(long timestamp, int count) {
		Assertion.assertExecutionState(timestamp >= latestTimestamp, this);
		if (timestamp > latestTimestamp) {
			int shift = (int) (timestamp - latestTimestamp);
			for (int i = trail.length - 1; i >= shift; i--) {
				trail[i] = trail[i - shift];
			}
			int zeroPrefixSize = Math.min(shift, trail.length);
			for (int i = 0; i < zeroPrefixSize; i++) {
				trail[i] = 0;
			}
			// Update initializedPrefixSize to take the shift into account.
			initializedPrefixSize = Math.min(initializedPrefixSize + shift, trail.length);
			latestTimestamp = timestamp;
		}
		if (trail.length > 0) {
			trail[0] = trail[0] + count;
		}
	}

	/**
	 * @param timestamp
	 * @param trailSize
	 * @return sum of the trailSize counts. if trailSize > currentTrailSize(timestamp), this information
	 * is partial because days before todayNumber - maxTrailSize(todayNumber) are not known.
	 */
	public int trailSum(long timestamp, int trailSize) {
		Assertion.assertExecutionState(trailSize <= trail.length, this);
		int sum = 0;
		int shift = (int) (timestamp - this.latestTimestamp);
		int truncatedTrail = Math.min(trailSize, trail.length);
		for (int i = 0; i < truncatedTrail - shift; i++) {
			sum = sum + trail[i];
		}
		return sum;
	}

	/**
	 * @param timestamp
	 * @return size of the trail prefix that contains valid counts.
	 */
	public int currentTrailSize(long timestamp) {
		int shift = (int) (timestamp - this.latestTimestamp);
		return Math.min(trail.length, initializedPrefixSize + shift);
	}

}
