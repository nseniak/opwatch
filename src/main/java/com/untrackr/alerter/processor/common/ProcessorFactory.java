package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariable;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeParseException;

public abstract class ProcessorFactory {

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String type();

	public abstract Processor make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError, IOException;

	public <T> T convertDescriptor(IncludePath path, Class<T> clazz, JsonDescriptor jsonObject) throws ValidationError {
		try {
			return processorService.getFactoryService().getObjectMapper().convertValue(jsonObject, clazz);
		} catch (IllegalArgumentException e) {
			String message = "invalid " + type() + " descriptor: " + e.getLocalizedMessage();
			throw new ValidationError(message, path, jsonObject);
		}
	}

	public <T> T checkFieldValue(IncludePath path, JsonDescriptor descriptor, String field, T value) throws ValidationError {
		if (value != null) {
			return value;
		} else {
			throw new ValidationError("missing " + type() + " field: \"" + field + "\"", path, descriptor);
		}
	}

	public <T> T optionalFieldValue(Object descriptor, String field, T value, T defaultValue) throws ValidationError {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public long durationValue(IncludePath path, JsonDescriptor jsonDescriptor, String fieldName, String delayString) {
		checkFieldValue(path, jsonDescriptor, fieldName, delayString);
		try {
			return Duration.parse(delayString).toMillis();
		} catch (DateTimeParseException e) {
			throw new ValidationError(e.getLocalizedMessage() + " at index " + e.getErrorIndex() + ": \"" + delayString + "\"", path, jsonDescriptor);
		}
	}

	public long optionalDurationValue(IncludePath path, JsonDescriptor jsonDescriptor, String fieldName, String delayString, long defaultValue) {
		if (delayString == null) {
			return defaultValue;
		} else {
			return durationValue(path, jsonDescriptor, fieldName, delayString);
		}
	}

	public String checkVariableSubstitution(IncludePath path, JsonDescriptor descriptor, String field, String text) throws ValidationError {
		try {
			return ApplicationUtil.substituteVariables(text);
		} catch (UndefinedSubstitutionVariable e) {
			throw new ValidationError("unknown variable in " + type() + " field \"" + field + "\": " + e.getName(), path, descriptor);
		}
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}
