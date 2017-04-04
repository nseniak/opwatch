package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.processor.config.*;

public class AlertGeneratorConfig extends ActiveProcessorConfig {

	// TODO Make StringValue
	private String priority = "medium";
	private StringValue message;
	private JavascriptPredicate trigger;
	private Boolean toggle = false;
	private String channel;

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
	public JavascriptPredicate getTrigger() {
		return trigger;
	}

	public void setTrigger(JavascriptPredicate trigger) {
		this.trigger = trigger;
	}

	@OptionalProperty
	public Boolean getToggle() {
		return toggle;
	}

	public void setToggle(Boolean toggle) {
		this.toggle = toggle;
	}

	@OptionalProperty
	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
