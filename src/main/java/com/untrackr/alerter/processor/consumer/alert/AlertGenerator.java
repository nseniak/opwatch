package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.processor.common.JavascriptPredicate;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGenerator extends Consumer {

	private Alert.Priority priority;
	private String title;
	private JavascriptPredicate predicate;
	private boolean toggle;
	private boolean toggleUp;
	private PushoverKey pushoverKey;


	public AlertGenerator(ProcessorService processorService, ScriptStack stack, PushoverKey pushoverKey, String title,
												Alert.Priority priority, JavascriptPredicate predicate, boolean toggle) {
		super(processorService, stack);
		this.priority = priority;
		this.title = title;
		this.predicate = predicate;
		this.toggle = toggle;
		this.pushoverKey = pushoverKey;
	}

	@Override
	public void consume(Payload payload) {
		boolean alert = (predicate == null) || predicate.call(payload, this);
		if (!toggle) {
			if (alert) {
				processorService.processorAlert(pushoverKey, priority, title, payload, this);
			}
		} else {
			if (!toggleUp && alert) {
				processorService.processorAlert(pushoverKey, priority, title, payload, this);
			} else if (toggleUp && !alert) {
				processorService.processorAlertEnd(pushoverKey, priority, title, payload, this);
			}
			toggleUp = alert;
		}
	}

	@Override
	public String identifier() {
		return title;
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

}
