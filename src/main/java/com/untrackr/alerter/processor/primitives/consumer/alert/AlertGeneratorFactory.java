package com.untrackr.alerter.processor.primitives.consumer.alert;

import com.untrackr.alerter.alert.Alert;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.processor.config.StringValue;
import com.untrackr.alerter.service.AlerterProfile;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGeneratorFactory extends ActiveProcessorFactory<AlertGeneratorConfig, AlertGenerator> {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alert";
	}

	@Override
	public Class<AlertGeneratorConfig> configurationClass() {
		return AlertGeneratorConfig.class;
	}

	@Override
	public AlertGenerator make(Object scriptObject) {
		AlertGeneratorConfig descriptor = convertProcessorDescriptor(scriptObject);
		String priorityName = checkPropertyValue("priority", descriptor.getPriority());
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new AlerterException("bad alert priority: \"" + priorityName + "\"", ExceptionContext.makeProcessorFactory(name()));
		}
		StringValue message = checkPropertyValue("message", descriptor.getMessage());
		JavascriptPredicate predicate = descriptor.getPredicate();
		boolean toggle = checkPropertyValue("toggle", descriptor.getToggle());
		AlerterProfile profile = processorService.profile();
		String applicationName = (descriptor.getApplication() != null) ? descriptor.getApplication() : profile.getDefaultPushoverApplication();
		String groupName = (descriptor.getGroup() != null) ? descriptor.getGroup() : profile.getDefaultPushoverGroup();
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), descriptor, name(), applicationName, groupName,
				message, priority, predicate, toggle);
		return alertGenerator;
	}

}
