package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;

public class TrailDescriptor extends ScheduledProcessorDescriptor {

	private JavascriptFilter transformer;
	private String duration;

	public JavascriptFilter getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptFilter transformer) {
		this.transformer = transformer;
	}

	@DefaultOption
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
