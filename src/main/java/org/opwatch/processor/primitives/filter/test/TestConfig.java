package org.opwatch.processor.primitives.filter.test;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.JavascriptPredicate;

public class TestConfig extends ActiveProcessorConfig {

	private JavascriptPredicate predicate;

	@ImplicitProperty
	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

}
