package com.untrackr.alerter.processor.primitives.filter.test;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.JavascriptPredicate;

public class TestConfig extends ActiveProcessorConfig {

	private JavascriptPredicate lambda;

	@ImplicitProperty
	public JavascriptPredicate getLambda() {
		return lambda;
	}

	public void setLambda(JavascriptPredicate lambda) {
		this.lambda = lambda;
	}

}
