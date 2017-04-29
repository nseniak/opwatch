package org.opwatch.processor.primitives.filter.count;

import org.opwatch.processor.config.*;

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
