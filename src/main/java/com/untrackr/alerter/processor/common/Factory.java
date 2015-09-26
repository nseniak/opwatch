package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariable;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.io.IOException;

public abstract class Factory {

	protected ProcessorService processorService;

	public Factory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String type();

	public abstract Processor make(JsonObject jsonObject, IncludePath path) throws ValidationError, IOException;

	public <T> T convertDescriptor(IncludePath path, Class<T> clazz, JsonObject jsonObject) throws ValidationError {
		try {
			return processorService.getFactoryService().getObjectMapper().convertValue(jsonObject, clazz);
		} catch (IllegalArgumentException e) {
			String message = "invalid " + type() + " descriptor: " + e.getLocalizedMessage();
			throw new ValidationError(message, path, jsonObject);
		}
	}

	public <T> T fieldValue(IncludePath path, JsonObject descriptor, String field, T value) throws ValidationError {
		if (value != null) {
			return value;
		} else {
			throw new ValidationError("missing " + type() + " field: \"" + field + "\"", path, descriptor);
		}
	}

	public <T> T optionalFieldValue(Object descriptor, String field, T value, T defaultValue) throws ValidationError {
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	public String checkVariableSubstitution(IncludePath path, JsonObject descriptor, String field, String text) throws ValidationError {
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
