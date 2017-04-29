package org.opwatch.processor.primitives.filter.jstack;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.OptionalProperty;

public class JstackConfig extends ActiveProcessorConfig {

	/**
	 * Regex for the method name we look for in the stack. Optional.
	 */
	private String methodRegex;

	@OptionalProperty
	public String getMethodRegex() {
		return methodRegex;
	}

	public void setMethodRegex(String methodRegex) {
		this.methodRegex = methodRegex;
	}

}
