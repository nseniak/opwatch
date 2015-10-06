package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGenerator extends Consumer {

	private Alert.Priority priority;
	private String title;

	public AlertGenerator(ProcessorService processorService, IncludePath path, String title, Alert.Priority priority) {
		super(processorService, path);
		this.priority = priority;
		this.title = title;
	}

	@Override
	public void consume(Payload payload) {
		processorService.processorAlert(priority, title, payload, this);
	}

	public String type() {
		return "alert";
	}

	public Alert.Priority getPriority() {
		return priority;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String identifier() {
		return title;
	}

}
