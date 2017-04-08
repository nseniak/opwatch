package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.processor.config.*;

public class AlertGeneratorConfig extends ActiveProcessorConfig {

	private String priority = "medium";
	private String title;
	private ConstantOrFilter<Object> body;
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
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@OptionalProperty
	public ConstantOrFilter<Object> getBody() {
		return body;
	}

	public void setBody(ConstantOrFilter<Object> body) {
		this.body = body;
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
