package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class TrailConfig extends ScheduledProcessorConfig {

	private String duration;

	@ImplicitProperty
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
