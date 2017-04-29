package org.opwatch.processor.primitives.producer.trail;

import org.opwatch.processor.config.Duration;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.ScheduledProcessorConfig;

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
