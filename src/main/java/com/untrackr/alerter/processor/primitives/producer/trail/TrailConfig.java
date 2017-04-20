package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.config.Duration;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class TrailConfig extends ScheduledProcessorConfig {

	private Duration duration;

	@ImplicitProperty
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

}
