package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class AlertGeneratorDesc extends ActiveProcessorDesc {

	private String priority;
	private String title;
	private String condition;
	private Boolean toggle;
	private String application;
	private String group;

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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Boolean getToggle() {
		return toggle;
	}

	public void setToggle(Boolean toggle) {
		this.toggle = toggle;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
