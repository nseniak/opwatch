package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;

public class TrailDescriptor extends ScheduledProcessorDescriptor {

	private String duration;

	@DefaultOption
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
