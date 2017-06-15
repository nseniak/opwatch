package org.opwatch.processor.primitives.filter.apply;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.JavascriptFilter;

public class ApplyConfig extends ActiveProcessorConfig {

	private JavascriptFilter output;

	@ImplicitProperty
	public JavascriptFilter getOutput() {
		return output;
	}

	public void setOutput(JavascriptFilter output) {
		this.output = output;
	}

}
