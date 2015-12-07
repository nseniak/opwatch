package com.untrackr.alerter.processor.consumer.alert;

import com.untrackr.alerter.model.common.Alert;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.model.common.PushoverKey;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.CompiledScript;

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
		String conditionSource = optionalFieldValue(path, jsonDescriptor, "condition", descriptor.getCondition(), null);
		CompiledScript conditionScript = (conditionSource == null) ? null : compileScript(path, jsonDescriptor, "test", conditionSource);
		boolean toggle = optionalFieldValue(path, jsonDescriptor, "toggle", descriptor.getToggle(), false);
		String applicationName = optionalFieldValue(path, jsonDescriptor, "application", descriptor.getApplication(), processorService.profile().getDefaultPushoverApplication());
		String groupName = optionalFieldValue(path, jsonDescriptor, "group", descriptor.getGroup(), processorService.profile().getDefaultPushoverGroup());
		PushoverKey pushoverKey = makeKey(applicationName, groupName, jsonDescriptor, path);
		AlertGenerator alertGenerator = new AlertGenerator(getProcessorService(), path, pushoverKey, title, priority, conditionScript, toggle) ;
		initialize(alertGenerator, descriptor);
		return alertGenerator;
	}

	public PushoverKey makeKey(String applicationName, String groupName, JsonDescriptor jsonDescriptor, IncludePath path) {
		try {
			return processorService.profile().getPushoverSettings().makeKey(applicationName, groupName);
		} catch (Throwable t) {
			throw new ValidationError(t, path, jsonDescriptor);
		}
	}

}
