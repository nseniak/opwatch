package org.opwatch.processor.primitives.filter.grep;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.OptionalProperty;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepConfig extends ActiveProcessorConfig {

	private NativeRegExp regexp;
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
