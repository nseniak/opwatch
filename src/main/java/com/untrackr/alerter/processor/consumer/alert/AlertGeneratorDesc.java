package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class AlertGeneratorDesc extends ActiveProcessorDesc {

	private String priority;
	private String title;

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}