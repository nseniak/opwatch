package com.untrackr.alerter.processor.primitives.transformer.jstack;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class JstackDescriptor extends ActiveProcessorDescriptor {

	/**
	 * Field name. Defaults to "text".
	 */
	private String field;
	/**
	 * Regex for the method name we look for in the stack. Optional.
	 */
	private String methodRegex;

	public String getField() {
		return field;
	}

	public String getMethodRegex() {
		return methodRegex;
	}

	public void setMethodRegex(String methodRegex) {
		this.methodRegex = methodRegex;
	}

}
