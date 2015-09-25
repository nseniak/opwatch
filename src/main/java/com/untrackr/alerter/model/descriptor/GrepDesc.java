package com.untrackr.alerter.model.descriptor;

public class GrepDesc extends ActiveProcessorDesc {

	/**
	 * Field name. Defaults to "text".
	 */
	private String field;
	/**
	 * Regex
	 */
	private String regex;
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

	public Boolean getInvert() {
		return invert;
	}

	public void setInvert(Boolean invert) {
		this.invert = invert;
	}

}
