package org.opwatch.processor.primitives.filter.apply;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.JavascriptFilter;

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
