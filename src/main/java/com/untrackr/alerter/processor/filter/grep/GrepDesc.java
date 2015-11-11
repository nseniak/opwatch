package com.untrackr.alerter.processor.filter.grep;

import com.untrackr.alerter.processor.common.ConditionalAlertGeneratorDesc;

public class GrepDesc extends ConditionalAlertGeneratorDesc {

	/**
	 * Field name. Defaults to "text".
	 */
	private String field;
	/**
	 * Regex. Mutually exclusive with regexes.
	 */
	private String regex;
	/**
	 * List of regexes. Mutually exclusive with regex.
	 */
	private String[] regexes;
	/**
	 * Exclude regex. Defaults to false.
	 */
	private Boolean invert;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String[] getRegexes() {
		return regexes;
	}

	public void setRegexes(String[] regexes) {
		this.regexes = regexes;
	}

	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
