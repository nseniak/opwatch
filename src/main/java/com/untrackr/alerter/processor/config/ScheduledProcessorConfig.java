package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.service.AlerterConfig;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private Duration period = AlerterConfig.defaultScheduledProducerPeriod();

	@OptionalProperty
	public Duration getPeriod() {
		return period;
	}

	public void setPeriod(Duration period) {
		this.period = period;
	}

}
