package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariableException;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;
import com.untrackr.alerter.service.ProcessorService;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ProcessorFactory<D extends ProcessorDescriptor, P extends Processor> {

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String name();

	public abstract Class<D> descriptorClass();

	public abstract P make(Object scriptObject);

	protected D convertProcessorDescriptor(Object scriptObject) {
		return (D) processorService.getScriptService().convertScriptValue(ValueLocation.makeArgument(name(), "descriptor"), descriptorClass(), scriptObject,
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

	protected <T> T optionalPropertyValue(String property, T value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected long durationValue(String fieldName, String delayString) {
		checkPropertyValue(fieldName, delayString);
		String duration;
		int start;
		if (delayString.startsWith("P") || delayString.startsWith("p")) {
			duration = delayString;
			start = 0;
		} else {
			duration = "pt" + delayString;
			start = 2;
		}
		try {
			return Duration.parse(duration).toMillis();
		} catch (DateTimeParseException e) {
			throw new AlerterException(e.getLocalizedMessage() + " at index " + (e.getErrorIndex() - start) + ": \"" + delayString + "\"",
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
		} catch (UndefinedSubstitutionVariableException e) {
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
