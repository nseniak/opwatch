package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepDescriptor extends ActiveProcessorDescriptor {

	/**
	 * Regex. Mutually exclusive with regexes.
	 */
	private NativeRegExp regexp;
	/**
	 * Exclude regex. Defaults to false.
	 */
	private Boolean invert = false;

	@DefaultOption
	public NativeRegExp getRegexp() {
		return regexp;
	}

	public void setRegexp(NativeRegExp regexp) {
		this.regexp = regexp;
	}

	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
