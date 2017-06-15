package org.opwatch.processor.config;

import org.opwatch.service.Config;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private Duration period = Config.defaultScheduledProducerPeriod();
	private Duration delay = Config.defaultScheduledProducerDelay();

	@OptionalProperty
	public Duration getPeriod() {
		return period;
	}

	public void setPeriod(Duration period) {
		this.period = period;
	}

	@OptionalProperty
	public Duration getDelay() {
		return delay;
	}

	public void setDelay(Duration delay) {
		this.delay = delay;
	}

}
