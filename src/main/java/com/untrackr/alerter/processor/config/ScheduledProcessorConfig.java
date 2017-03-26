package com.untrackr.alerter.processor.config;

import com.untrackr.alerter.service.AlerterProfile;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private String period = AlerterProfile.defaultScheduledProducerPeriod();

	@OptionalProperty
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
