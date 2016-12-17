package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.*;
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
	public AlertGenerator make(Object scriptObject) {
		AlertGeneratorDesc descriptor = convertProcessorArgument(AlertGeneratorDesc.class, scriptObject);
		String priorityName = optionaPropertyValue("priority", descriptor.getPriority(), "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new AlerterException("bad alert priority: \"" + priorityName + "\"", ExceptionContext.makeProcessorFactory(type()));
		}
		StringValue message = checkPropertyValue("message", descriptor.getMessage());
		JavascriptPredicate predicate = optionaPropertyValue("predicate", descriptor.getPredicate(), null);
		boolean toggle = optionaPropertyValue("toggle", descriptor.getToggle(), false);
		String applicationName = optionaPropertyValue("application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionaPropertyValue("group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), descriptor, type(), applicationName, groupName,
				message, priority, predicate, toggle);
		return alertGenerator;
	}

}
