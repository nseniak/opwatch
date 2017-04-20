package com.untrackr.alerter.processor.primitives.filter.count;

import com.untrackr.alerter.processor.config.*;

public class CountConfig extends ScheduledProcessorConfig {

	private JavascriptPredicate predicate;
	private Duration duration;

	@OptionalProperty
	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

	@ImplicitProperty
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

}
