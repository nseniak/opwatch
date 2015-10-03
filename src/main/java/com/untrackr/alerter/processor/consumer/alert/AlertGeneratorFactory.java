package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGeneratorFactory extends ActiveProcessorFactory {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "alert";
	}

	@Override
	public Processor make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		AlertGeneratorDesc descriptor = convertDescriptor(path, AlertGeneratorDesc.class, jsonDescriptor);
		String priorityName = optionalFieldValue(path, jsonDescriptor, "priority", descriptor.getPriority(), "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new ValidationError("bad alert priority: \"" + priorityName + "\"", path, jsonDescriptor);
		}
		String title = checkFieldValue(path, jsonDescriptor, "title", descriptor.getTitle());
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), priority, title, path);
		initialize(alertGenerator, descriptor);
		return alertGenerator;
	}

}
