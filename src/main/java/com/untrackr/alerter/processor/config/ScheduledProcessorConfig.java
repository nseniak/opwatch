package com.untrackr.alerter.processor.config;

public class ScheduledProcessorConfig extends ActiveProcessorConfig {

	private String period;

	@OptionalProperty
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
