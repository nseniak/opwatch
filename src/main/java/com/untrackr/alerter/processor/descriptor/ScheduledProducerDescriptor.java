package com.untrackr.alerter.processor.descriptor;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class ScheduledProducerDescriptor extends ActiveProcessorDescriptor {

	private String period;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

}
