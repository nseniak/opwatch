package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.StringValue;
import com.untrackr.alerter.processor.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGenerator extends Consumer {

	private Alert.Priority priority;
	private StringValue message;
	private JavascriptPredicate predicate;
	private boolean toggle;
	private boolean toggleUp;
	private String application;
	private String group;


	public AlertGenerator(ProcessorService processorService, String name, String application, String group, StringValue message,
												Alert.Priority priority, JavascriptPredicate predicate, boolean toggle) {
		super(processorService, name);
		this.priority = priority;
		this.message = message;
		this.predicate = predicate;
		this.toggle = toggle;
		this.application = application;
		this.group = group;
	}

	@Override
	public void consume(Payload payload) {
		boolean alert = (predicate == null) || predicate.call(payload, this);
		if (!toggle) {
			if (alert) {
				processorService.processorAlert(priority, message.value(this, payload), this);
			}
		} else {
			if (!toggleUp && alert) {
				processorService.processorAlert(priority, message.value(this, payload), this);
			} else if (toggleUp && !alert) {
				processorService.processorAlertEnd(priority, message.value(this, payload), this);
			}
			toggleUp = alert;
		}
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	public String getApplication() {
		return application;
	}

	public String getGroup() {
		return group;
	}

}
