package com.untrackr.alerter.processor.transformer.grep;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

import java.util.List;

public class GrepDesc extends ActiveProcessorDesc {

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
