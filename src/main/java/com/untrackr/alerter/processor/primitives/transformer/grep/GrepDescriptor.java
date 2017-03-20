package com.untrackr.alerter.processor.primitives.transformer.grep;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

import java.util.List;

public class GrepDescriptor extends ActiveProcessorDescriptor {

	/**
	 * Regex. Mutually exclusive with regexes.
	 */
	private String regex;
	/**
	 * List of regexes. Mutually exclusive with regex.
	 */
	private List<String> regexes;
	/**
	 * Exclude regex. Defaults to false.
	 */
	private Boolean invert = false;

	@DefaultOption
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public List<String> getRegexes() {
		return regexes;
	}

	public void setRegexes(List<String> regexes) {
		this.regexes = regexes;
	}

	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
