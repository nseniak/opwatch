package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.JavascriptPredicate;

public class AlertGeneratorDesc extends ActiveProcessorDesc {

	private String priority;
	private String title;
	private JavascriptPredicate predicate;
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

	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
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
