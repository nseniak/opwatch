package com.untrackr.alerter.processor.descriptor;

public class ScheduledProcessorDescriptor extends ActiveProcessorDescriptor {

	private String period;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
