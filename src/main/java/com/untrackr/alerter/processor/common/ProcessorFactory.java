package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariable;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ProcessorFactory {

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public abstract String name();

	public abstract Processor make(Object scriptObject);

	protected <T> T convertProcessorArgument(Class<T> clazz, Object scriptObject) {
		return (T) convertScriptValue(ConvertedValueSource.makeArgument(), clazz, scriptObject);
	}

	public Object convertScriptValue(ConvertedValueSource valueSource, Type type, Object scriptValue) {
		if (scriptValue == null) {
			return null;
		} else if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isAssignableFrom(scriptValue.getClass())) {
				return scriptValue;
			} else if (scriptValue instanceof ScriptObjectMirror) {
				ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
				if ((type == JavascriptTransformer.class) && scriptObject.isFunction()) {
					return new JavascriptTransformer(scriptObject);
				} else if (type == JavascriptPredicate.class) {
					return new JavascriptPredicate(scriptObject);
				} else if (type == JavascriptProducer.class) {
					return new JavascriptProducer(scriptObject);
				} else {
					Object value = BeanUtils.instantiate(clazz);
					BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
					for (String key : scriptObject.getOwnKeys(true)) {
						try {
							PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(key);
							Type fieldType = descriptor.getReadMethod().getGenericReturnType();
							wrapper.setPropertyValue(key, convertScriptValue(ConvertedValueSource.makeField(key), fieldType, scriptObject.get(key)));
						} catch (InvalidPropertyException e) {
							throw new RuntimeScriptException(name() + ": invalid property name \"" + key + "\"");
						}
					}
					return value;
				}
			}
		} else {
			Type listType = parameterizedListType(type);
			if (listType != null) {
				try {
					ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
					List<Object> scriptList = (List<Object>) ScriptUtils.convert(scriptObject, List.class);
					List<Object> list = new ArrayList<>();
					for (Object scriptListObject : scriptList) {
						list.add(convertScriptValue(valueSource.toListElement(), listType, scriptListObject));
					}
					return list;
				} catch (ClassCastException e) {
					// Nothing to do
				}
			}
		}
		throw new RuntimeScriptException(name() + ": invalid " + valueSource.describe() + ", expected " + typeName(type) + ", got: " + scriptValue.toString());
	}

	private String typeName(Type type) {
		if (type instanceof Class) {
			return simpleClassName((Class) type);
		}
		Type listType = parameterizedListType(type);
		if (listType != null) {
			return typeName(listType) + " array";
		} else {
			return type.toString();
		}
	}

	private String simpleClassName(Class<?> clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			return "a string";
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return "an integer";
		} else if (Number.class.isAssignableFrom(clazz)) {
			return "a number";
		} else if (JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (ProcessorDesc.class.isAssignableFrom(clazz)) {
			return "a processor descriptor";
		} else {
			return "a " + clazz.getSimpleName();
		}
	}

	private Type parameterizedListType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] args = paramType.getActualTypeArguments();
			if ((paramType.getRawType() == List.class) && (args.length == 1)) {
				return args[0];
			}
		}
		return null;
	}

	public <T> T checkFieldValue(String field, T value) throws RuntimeScriptException {
		if (value != null) {
			return value;
		} else {
			throw new RuntimeScriptException(name() + ": missing field \"" + field + "\" in descriptor");
		}
	}

	public <T> T optionalFieldValue(String field, T value, T defaultValue) throws RuntimeScriptException {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public long durationValue(String fieldName, String delayString) {
		checkFieldValue(fieldName, delayString);
		try {
			return Duration.parse(delayString).toMillis();
		} catch (DateTimeParseException e) {
			throw new RuntimeScriptException(name() + ":" + e.getLocalizedMessage() + " at index " + e.getErrorIndex() + ": \"" + delayString + "\"");
		}
	}

	public long optionalDurationValue(String fieldName, String delayString, long defaultValue) {
		if (delayString == null) {
			return defaultValue;
		} else {
			return durationValue(fieldName, delayString);
		}
	}

	public String checkVariableSubstitution(String field, String text) throws RuntimeScriptException {
		try {
			return ApplicationUtil.substituteVariables(text);
		} catch (UndefinedSubstitutionVariable e) {
			throw new RuntimeScriptException(name() + ": unknown variable in " + name() + " field \"" + field + "\": " + e.getName());
		}
	}

	public Pattern compilePattern(String field, String regex) throws RuntimeScriptException {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new RuntimeScriptException(name() + ": invalid regex in field \"" + field + "\":" + e.getMessage());
		}
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}
