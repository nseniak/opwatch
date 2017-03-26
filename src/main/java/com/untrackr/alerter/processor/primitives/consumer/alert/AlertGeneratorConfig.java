package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.processor.config.*;

public class AlertGeneratorConfig extends ActiveProcessorConfig {

	private String priority = "normal";
	private StringValue message;
	private JavascriptPredicate predicate;
	private Boolean toggle = false;
	private String application;
	private String group;

	@OptionalProperty
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@ImplicitProperty
	public StringValue getMessage() {
		return message;
	}

	public void setMessage(StringValue message) {
		this.message = message;
	}

	@OptionalProperty
	public JavascriptPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(JavascriptPredicate predicate) {
		this.predicate = predicate;
	}

	@OptionalProperty
	public Boolean getToggle() {
		return toggle;
	}

	public void setToggle(Boolean toggle) {
		this.toggle = toggle;
	}

	@OptionalProperty
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	@OptionalProperty
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
