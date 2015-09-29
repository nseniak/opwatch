package com.untrackr.alerter.processor.filter.once;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class OnceFactory extends ActiveProcessorFactory {

	public OnceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "once";
	}

	@Override
	public Once make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		OnceDesc descriptor = convertDescriptor(path, OnceDesc.class, jsonObject);
		String delayString = fieldValue(path, jsonObject, "delay", descriptor.getDelay());
		long delay;
		try {
			delay = Duration.parse(delayString).toMillis();
		} catch (DateTimeParseException e) {
			throw new ValidationError(e.getLocalizedMessage() + " at index " + e.getErrorIndex() + ": \"" + delayString + "\"", path, jsonObject);
		}
		Once once = new Once(getProcessorService(), path, delay);
		initialize(once, descriptor);
		return once;
	}

}
