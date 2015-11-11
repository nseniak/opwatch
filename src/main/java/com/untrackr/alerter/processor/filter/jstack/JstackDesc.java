package com.untrackr.alerter.processor.filter.jstack;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class JstackDesc extends ActiveProcessorDesc {

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
