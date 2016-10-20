package com.untrackr.alerter.processor.producer.count;

import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class CountDesc extends ScheduledProducerDesc {

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
