package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class TrailDesc extends ScheduledProducerDesc {

	private String value;
	private String duration;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
