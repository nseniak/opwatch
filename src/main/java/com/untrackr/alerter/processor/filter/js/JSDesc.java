package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.processor.common.ConditionalAlertGeneratorDesc;

public class JSDesc extends ConditionalAlertGeneratorDesc {

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
