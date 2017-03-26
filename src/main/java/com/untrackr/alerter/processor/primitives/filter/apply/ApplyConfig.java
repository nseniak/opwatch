package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.JavascriptFilter;

public class ApplyConfig extends ActiveProcessorConfig {

	private JavascriptFilter lambda;

	@ImplicitProperty
	public JavascriptFilter getLambda() {
		return lambda;
	}

	public void setLambda(JavascriptFilter lambda) {
		this.lambda = lambda;
	}

}
