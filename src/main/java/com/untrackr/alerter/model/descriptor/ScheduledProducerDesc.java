package com.untrackr.alerter.model.descriptor;

public class ScheduledProducerDesc extends ActiveProcessorDesc {

	private Long period;

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

}
