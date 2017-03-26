package com.untrackr.alerter.processor.primitives.filter.grep;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.OptionalProperty;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepConfig extends ActiveProcessorConfig {

	/**
	 * Regex. Mutually exclusive with regexes.
	 */
	private NativeRegExp regexp;
	/**
	 * Exclude regex. Defaults to false.
	 */
	private Boolean invert = false;

	@ImplicitProperty
	public NativeRegExp getRegexp() {
		return regexp;
	}

	public void setRegexp(NativeRegExp regexp) {
		this.regexp = regexp;
	}

	@OptionalProperty
	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
