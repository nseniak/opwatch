package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariable;
import com.untrackr.alerter.service.ProcessorService;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ProcessorFactory {

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String name();

	public String displayName(ProcessorDesc processorDesc) {
		if (processorDesc.getName() != null) {
			return processorDesc.getName();
		} else {
			return name();
		}
	}

	public abstract Processor make(Object scriptObject);

	protected <T> T convertProcessorArgument(Class<T> clazz, Object scriptObject) {
		return (T) processorService.getScriptService().convertScriptValue(ValueLocation.makeArgument(name()), clazz, scriptObject,
				() -> ExceptionContext.makeProcessorFactory(name()));
	}

	public <T> T checkPropertyValue(String property, T value) {
		if (value != null) {
			return value;
		} else {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new AlerterException("missing " + location.describeAsValue(), ExceptionContext.makeProcessorFactory(name()));
		}
	}

	public <T> T optionaPropertyValue(String property, T value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public long durationValue(String fieldName, String delayString) {
		checkPropertyValue(fieldName, delayString);
		try {
			return Duration.parse(delayString).toMillis();
		} catch (DateTimeParseException e) {
			throw new AlerterException(e.getLocalizedMessage() + " at index " + e.getErrorIndex() + ": \"" + delayString + "\"",
					ExceptionContext.makeProcessorFactory(name()));
		}
	}

	public long optionalDurationValue(String fieldName, String delayString, long defaultValue) {
		if (delayString == null) {
			return defaultValue;
		} else {
			return durationValue(fieldName, delayString);
		}
	}

	public String checkVariableSubstitution(String property, String text) {
		try {
			return ApplicationUtil.substituteVariables(text);
		} catch (UndefinedSubstitutionVariable e) {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new AlerterException("unknown variable in " + location.describeAsValue() + ": " + e.getName(),
					ExceptionContext.makeProcessorFactory(name()));
		}
	}

	public Pattern compilePattern(String property, String regex) {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new AlerterException("invalid regex in " + location.describeAsValue() + ":" + e.getMessage(),
					ExceptionContext.makeProcessorFactory(name()));
		}
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}
