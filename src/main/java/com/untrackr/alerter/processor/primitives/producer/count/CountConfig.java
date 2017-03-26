package com.untrackr.alerter.processor.primitives.producer.count;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.config.OptionalProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;

public class CountConfig extends ScheduledProcessorConfig {

	private JavascriptPredicate predicate;
	private String duration;

	@ImplicitProperty
	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

	@OptionalProperty
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
