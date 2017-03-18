package com.untrackr.alerter.processor.primitives.producer.count;

import com.untrackr.alerter.processor.descriptor.JavascriptPredicate;
import com.untrackr.alerter.processor.descriptor.ScheduledProducerDescriptor;

public class CountDescriptor extends ScheduledProducerDescriptor {

	private JavascriptPredicate predicate;
	private String duration;

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
