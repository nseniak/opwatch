package org.opwatch.processor.config;

import org.opwatch.service.Config;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private Duration period = Config.defaultScheduledProducerPeriod();

	@OptionalProperty
	public Duration getPeriod() {
		return period;
	}

	public void setPeriod(Duration period) {
		this.period = period;
	}

}
