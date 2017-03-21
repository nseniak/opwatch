package com.untrackr.alerter.processor.primitives.producer.count;

import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;
import com.untrackr.alerter.processor.descriptor.ScheduledProcessorDescriptor;

public class CountDescriptor extends ScheduledProcessorDescriptor {

	private JavascriptPredicate predicate;
	private String duration;

	@DefaultOption
	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
