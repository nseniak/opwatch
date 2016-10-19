package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class AlertGeneratorFactory extends ActiveProcessorFactory {

	public AlertGeneratorFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alert";
	}

	@Override
	public Processor make(Object scriptObject) {
		AlertGeneratorDesc descriptor = convertProcessorArgument(AlertGeneratorDesc.class, scriptObject);
		String priorityName = optionaPropertyValue("priority", descriptor.getPriority(), "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new AlerterException("bad alert priority: \"" + priorityName + "\"", ExceptionContext.makeProcessorFactory(name()));
		}
		StringValue message = checkPropertyValue("message", descriptor.getMessage());
		JavascriptPredicate predicate = optionaPropertyValue("predicate", descriptor.getPredicate(), null);
		boolean toggle = optionaPropertyValue("toggle", descriptor.getToggle(), false);
		String applicationName = optionaPropertyValue("application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionaPropertyValue("group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		PushoverKey pushoverKey = makeKey(applicationName, groupName);
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), displayName(descriptor), pushoverKey, message, priority, predicate, toggle);
		return alertGenerator;
	}

	public PushoverKey makeKey(String applicationName, String groupName) {
		try {
			return processorService.profile().getPushoverSettings().makeKey(applicationName, groupName);
		} catch (Throwable t) {
			throw new AlerterException(t.getMessage(), ExceptionContext.makeProcessorFactory(name()));
		}
	}

}
