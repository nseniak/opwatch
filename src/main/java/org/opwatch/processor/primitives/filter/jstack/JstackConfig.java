package org.opwatch.processor.primitives.filter.jstack;

import jdk.nashorn.internal.objects.NativeRegExp;
import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.OptionalProperty;

public class JstackConfig extends ActiveProcessorConfig {

	/**
	 * Regex for the method name we look for in the stack.
	 */
	private NativeRegExp methodRegexp;

	@OptionalProperty
	public NativeRegExp getMethodRegexp() {
		return methodRegexp;
	}

	public void setMethodRegexp(NativeRegExp methodRegexp) {
		this.methodRegexp = methodRegexp;
	}

}
