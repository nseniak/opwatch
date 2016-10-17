package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class TrailDesc extends ScheduledProducerDesc {

	private JavascriptTransformer transformer;
	private String duration;

	public JavascriptTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptTransformer transformer) {
		this.transformer = transformer;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
