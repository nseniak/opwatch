package com.untrackr.alerter.processor.filter.collect;

import com.untrackr.alerter.processor.common.ConditionalAlertGeneratorDesc;

public class CollectDesc extends ConditionalAlertGeneratorDesc {

	private String value;
	private Integer count;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
