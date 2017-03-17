package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGeneratorFactory extends ActiveProcessorFactory<AlertGeneratorDesc, AlertGenerator> {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "alert";
	}

	@Override
	public Class<AlertGeneratorDesc> descriptorClass() {
		return AlertGeneratorDesc.class;
	}

	@Override
	public AlertGenerator make(Object scriptObject) {
		AlertGeneratorDesc descriptor = convertProcessorDescriptor(scriptObject);
		String priorityName = optionalPropertyValue("priority", descriptor.getPriority(),  "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new AlerterException("bad alert priority: \"" + priorityName + "\"", ExceptionContext.makeProcessorFactory(type()));
		}
		StringValue message = checkPropertyValue("message", descriptor.getMessage());
		JavascriptPredicate predicate = descriptor.getPredicate();
		boolean toggle = optionalPropertyValue("toggle", descriptor.getToggle(),  false);
		String applicationName = optionalPropertyValue("application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionalPropertyValue("group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), descriptor, type(), applicationName, groupName,
				message, priority, predicate, toggle);
		return alertGenerator;
	}

}
