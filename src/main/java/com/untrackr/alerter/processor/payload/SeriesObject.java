package com.untrackr.alerter.processor.payload;

public class SeriesObject {

	private Object value;
	private long stamp;

	public SeriesObject(Object value, long stamp) {
		this.value = value;
		this.stamp = stamp;
	}

	public Object getValue() {
		return value;
	}

	public long getStamp() {
		return stamp;
	}

}
