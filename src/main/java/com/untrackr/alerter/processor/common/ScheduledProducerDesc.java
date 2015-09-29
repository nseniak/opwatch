package com.untrackr.alerter.processor.common;

public class ScheduledProducerDesc extends ActiveProcessorDesc {

	private Long period;

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

}
