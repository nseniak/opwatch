package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;

public class AlertGenerator extends Consumer {

	private Alert.Priority priority;
	private String title;
	private CompiledScript condition;
	private boolean toggle;
	private Bindings bindings;
	private boolean toggleUp;


	public AlertGenerator(ProcessorService processorService, IncludePath path, String title, Alert.Priority priority,
												CompiledScript condition, boolean toggle) {
		super(processorService, path);
		this.priority = priority;
		this.title = title;
		this.condition = condition;
		this.toggle = toggle;
		this.bindings = processorService.getNashorn().createBindings();
	}

	@Override
	public void consume(Payload payload) {
		boolean alert = (condition == null) || scriptBooleanValue(condition, bindings, payload);
		if (!toggle) {
			if (alert) {
				processorService.processorAlert(priority, title, payload, this);
			}
		} else {
			if (!toggleUp && alert) {
				processorService.processorAlert(priority, title, payload, this);
			} else if (toggleUp && !alert) {
				processorService.processorAlertEnd(priority, title, payload, this);
			}
			toggleUp = alert;
		}
	}

	public String type() {
		return "alert";
	}

	public void setCondition(CompiledScript condition) {
		this.condition = condition;
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
