package com.untrackr.alerter.processor.consumer;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.AlertGeneratorDesc;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.Factory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGeneratorFactory extends Factory {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "alert";
	}

	@Override
	public Processor make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		AlertGeneratorDesc descriptor = convertDescriptor(path, AlertGeneratorDesc.class, jsonObject);
		String priorityName = optionalFieldValue(descriptor, "priority", descriptor.getPriority(), "normal");
		Alert.Priority priority = Alert.Priority.valueOf(priorityName);
		if (priority == null) {
			throw new ValidationError("bad alert priority: \"" + priorityName + "\"", path, jsonObject);
		}
		String title = fieldValue(path, jsonObject, "title", descriptor.getTitle());
		return new AlertGenerator(getProcessorService(), priority, title, path);
	}

}
