package org.opwatch.processor.payload;

public class SeriesObject {

	private Object value;
	private Long timestamp;

	public SeriesObject() {
	}

	public SeriesObject(Object value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
