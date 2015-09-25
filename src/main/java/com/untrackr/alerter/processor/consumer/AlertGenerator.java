package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGenerator extends Consumer {

	private Alert.Priority priority;
	private String title;

	public AlertGenerator(ProcessorService processorService, Alert.Priority priority, String title, IncludePath path) {
		super(processorService, path);
		this.priority = priority;
		this.title = title;
	}

	@Override
	public void initialize() {
		// Nothing to do
	}

	@Override
	public void consume(Payload payload) {
		processorService.processorAlert(priority, title, payload, this);
	}

	@Override
	public String descriptor() {
		return "alert{}";
	}

	public Alert.Priority getPriority() {
		return priority;
	}

	public String getTitle() {
		return title;
	}

}
