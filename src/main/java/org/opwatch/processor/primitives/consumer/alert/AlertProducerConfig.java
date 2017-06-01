package org.opwatch.processor.primitives.consumer.alert;

import org.opwatch.processor.config.*;

public class AlertProducerConfig extends ActiveProcessorConfig {

	private String level = "medium";
	private String title;
	private ValueOrFilter<Object> details;
	private JavascriptPredicate trigger;
	private Boolean toggle = false;
	private String channel;

	@OptionalProperty
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@ImplicitProperty
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@OptionalProperty
	public ValueOrFilter<Object> getDetails() {
		return details;
	}

	public void setDetails(ValueOrFilter<Object> details) {
		this.details = details;
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
