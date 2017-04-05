package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariableException;
import com.untrackr.alerter.processor.config.*;
import com.untrackr.alerter.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ProcessorFactory<C extends ProcessorConfig, P extends Processor> {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorFactory.class);

	private String id;

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.id = processorService.uuid();
		this.processorService = processorService;
	}

	public abstract String name();

	public abstract Class<C> configurationClass();

	public abstract Class<P> processorClass();

	public ProcessorSignature staticSignature() {
		return null;
	}

	public abstract P make(Object scriptObject);

	public ProcessorSchema schema() {
		try {
			ProcessorConfig instance = configurationClass().newInstance();
			List<ConfigPropertySchema> properties = new ArrayList<>();
			for (PropertyDescriptor pd : Introspector.getBeanInfo(configurationClass()).getPropertyDescriptors()) {
				String name = pd.getName();
				if (!name.equals("class")) {
					ConfigPropertySchema schema = new ConfigPropertySchema();
					schema.setName(name);
					Method readMethod = pd.getReadMethod();
					schema.setType(processorService.getScriptService().typeName(readMethod.getGenericReturnType()));
					schema.setImplicit(readMethod.getAnnotation(ImplicitProperty.class) != null);
					boolean optional = readMethod.getAnnotation(OptionalProperty.class) != null;
					if (optional) {
						schema.setOptional(true);
						schema.setDefaultValue(readMethod.invoke(instance));
					}
					properties.add(schema);
				}
			}
			ProcessorSchema config = new ProcessorSchema();
			config.setName(name());
			config.setProperties(properties);
			config.setCategory(processorService.getScriptService().processorCategoryName(this));
			return config;
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | IntrospectionException e) {
			logger.error("Exception while fetching properties: " + configurationClass(), e);
			throw new RuntimeError("cannot fetch properties", new FactoryExecutionContext(this));
		}
	}

	public void error(String message) {
		throw new RuntimeError(message, new FactoryExecutionContext(this));
	}

	protected C convertProcessorConfig(Object scriptObject) {
		return (C) processorService.getScriptService().convertScriptValue(ValueLocation.makeArgument(name(), "configuration"), configurationClass(), scriptObject,
				(message) -> new RuntimeError(message, new FactoryExecutionContext(this)));
	}

	public <T> T checkPropertyValue(String property, T value) {
		if (value != null) {
			return value;
		} else {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new RuntimeError("missing " + location.describeAsValue(), new FactoryExecutionContext(this));
		}
	}

	protected <T> T optionalPropertyValue(String property, T value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	protected long durationValue(String durationString) {
		String duration;
		int start;
		if (durationString.startsWith("P") || durationString.startsWith("p")) {
			duration = durationString;
			start = 0;
		} else {
			duration = "pt" + durationString;
			start = 2;
		}
		try {
			return Duration.parse(duration).toMillis();
		} catch (DateTimeParseException e) {
			throw new RuntimeError(e.getLocalizedMessage() + " at index " + (e.getErrorIndex() - start) + ": \"" + durationString + "\"",
					new FactoryExecutionContext(this));
		}
	}

	public String checkVariableSubstitution(String property, String text) {
		try {
			return ApplicationUtil.substituteVariables(text);
		} catch (UndefinedSubstitutionVariableException e) {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new RuntimeError("unknown variable in " + location.describeAsValue() + ": " + e.getName(), new FactoryExecutionContext(this));
		}
	}

	public Pattern compilePattern(String property, String regex) {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new RuntimeError("invalid regex in " + location.describeAsValue() + ":" + e.getMessage(),
					new FactoryExecutionContext(this));
		}
	}

	public String getId() {
		return id;
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}
