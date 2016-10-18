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
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		AlertGeneratorDesc descriptor = convertProcessorArgument(AlertGeneratorDesc.class, scriptObject);
		String priorityName = optionalFieldValue("priority", descriptor.getPriority(), "normal");
		Alert.Priority priority;
		try {
			priority = Alert.Priority.valueOf(priorityName);
		} catch (IllegalArgumentException e) {
			throw new RuntimeScriptException("bad alert priority: \"" + priorityName + "\"");
		}
		String title = checkFieldValue("title", descriptor.getTitle());
		JavascriptPredicate predicate = optionalFieldValue("predicate", descriptor.getPredicate(), null);
		boolean toggle = optionalFieldValue("toggle", descriptor.getToggle(), false);
		String applicationName = optionalFieldValue("application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionalFieldValue("group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		PushoverKey pushoverKey = makeKey(applicationName, groupName);
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), ScriptStack.currentStack(), pushoverKey, title, priority, predicate, toggle);
		initialize(alertGenerator, descriptor);
		return alertGenerator;
	}

	public PushoverKey makeKey(String applicationName, String groupName) {
		try {
			return processorService.profile().getPushoverSettings().makeKey(applicationName, groupName);
		} catch (Throwable t) {
			throw new RuntimeScriptException(t.getMessage());
		}
	}

}
