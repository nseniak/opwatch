package com.untrackr.alerter.processor.filter.jsgrep;

import com.untrackr.alerter.processor.common.ConditionalAlertGeneratorDesc;

public class JSGrepDesc extends ConditionalAlertGeneratorDesc {

	private String test;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

}
