package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.JsonDescriptor;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor scriptDescriptor = scriptDescriptor(object);
		AlertGeneratorDesc descriptor = convertScriptDescriptor(AlertGeneratorDesc.class, scriptDescriptor);
		String priorityName = optionalFieldValue(scriptDescriptor, "priority", descriptor.getPriority(), "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new ValidationError("bad alert priority: \"" + priorityName + "\"", scriptDescriptor);
		}
		String title = checkFieldValue(scriptDescriptor, "title", descriptor.getTitle());
		JavascriptPredicate predicate = optionalFieldValue(scriptDescriptor, "predicate", descriptor.getPredicate(), null);
		boolean toggle = optionalFieldValue(scriptDescriptor, "toggle", descriptor.getToggle(), false);
		String applicationName = optionalFieldValue(scriptDescriptor, "application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionalFieldValue(scriptDescriptor, "group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		PushoverKey pushoverKey = makeKey(applicationName, groupName, scriptDescriptor);
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), ScriptStack.currentStack(), pushoverKey, title, priority, predicate, toggle);
		initialize(alertGenerator, descriptor);
		return alertGenerator;
	}

	public PushoverKey makeKey(String applicationName, String groupName, JsonDescriptor jsonDescriptor) {
		try {
			return processorService.profile().getPushoverSettings().makeKey(applicationName, groupName);
		} catch (Throwable t) {
			throw new ValidationError(t.getMessage(), jsonDescriptor);
		}
	}

}
