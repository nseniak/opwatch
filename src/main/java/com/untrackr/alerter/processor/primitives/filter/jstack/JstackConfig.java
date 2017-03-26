package com.untrackr.alerter.processor.primitives.filter.jstack;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.OptionalProperty;

public class JstackConfig extends ActiveProcessorConfig {

	/**
	 * Field name. Defaults to "text".
	 */
	private String field;
	/**
	 * Regex for the method name we look for in the stack. Optional.
	 */
	private String methodRegex;

	@OptionalProperty
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@OptionalProperty
	public String getMethodRegex() {
		return methodRegex;
	}

	public void setMethodRegex(String methodRegex) {
		this.methodRegex = methodRegex;
	}

}
