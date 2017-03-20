package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariableException;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ProcessorFactory<D extends ProcessorDescriptor, P extends Processor> {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorFactory.class);

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String name();

	public abstract Class<D> descriptorClass();

	public abstract P make(Object scriptObject);

	public List<String> properties() {
		try {
			List<String> props = new ArrayList<>();
			BeanInfo info = Introspector.getBeanInfo(descriptorClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				String name = pd.getName();
				if (!name.equals("class")) {
					props.add(name);
				}
			}
			return props;
		} catch (IntrospectionException e) {
			logger.error("Exception while fetching properties: " + descriptorClass(), e);
			throw new AlerterException("cannot fetch properties", ExceptionContext.makeProcessorFactory(name()));
		}
	}

	public String defaultProperty() {
		try {
			BeanInfo info = Introspector.getBeanInfo(descriptorClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				if (pd.getReadMethod().getAnnotation(DefaultOption.class) != null) {
					return pd.getName();
				}
			}
		} catch (IntrospectionException e) {
			// Nothing to do
		}
		return null;
	}

	public void error(String message) {
		throw new AlerterException(message, ExceptionContext.makeProcessorFactory(name()));
	}

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
