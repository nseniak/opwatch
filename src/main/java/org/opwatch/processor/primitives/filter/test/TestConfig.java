package org.opwatch.processor.primitives.filter.test;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.JavascriptPredicate;

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
