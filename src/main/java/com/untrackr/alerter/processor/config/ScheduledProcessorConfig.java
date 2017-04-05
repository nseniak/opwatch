package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.service.AlerterConfig;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private String period = AlerterConfig.defaultScheduledProducerPeriod();

	@OptionalProperty
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
