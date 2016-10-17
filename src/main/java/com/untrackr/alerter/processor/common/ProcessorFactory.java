package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.common.UndefinedSubstitutionVariable;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

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

	public abstract Processor make(Object argument);

	public JsonDescriptor scriptDescriptor(Object object) {
		if (object instanceof ScriptObjectMirror) {
			return new JsonDescriptor((ScriptObjectMirror) object);
		} else {
			throw new RuntimeScriptError(name() + ": invalid argument type: " + scriptTypeName(object));
		}
	}

	private String scriptTypeName(Object object) {
		if (object instanceof String) {
			return "string";
		} else if (object instanceof Number) {
			return "number";
		} else {
			return object.getClass().getName();
		}
	}

	public <T> T convertScriptDescriptor(Class<T> clazz, JsonDescriptor jsonObject) {
		return (T) convertScriptValue(clazz, jsonObject.getObjectMirror());
	}

	private Object convertScriptValue(Type type, Object scriptValue) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isAssignableFrom(scriptValue.getClass())) {
				return scriptValue;
			}
			if (!(scriptValue instanceof ScriptObjectMirror)) {
				throw new ValidationError(name() + ": cannot convert object to " + clazz.getSimpleName(), null);
			}
			ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
			if (type == JavascriptTransformer.class) {
				return new JavascriptTransformer(name(), scriptObject);
			}
			if (type == JavascriptPredicate.class) {
				return new JavascriptPredicate(name(), scriptObject);
			}
			if (type == JavascriptGenerator.class) {
				return new JavascriptGenerator(name(), scriptObject);
			}
			Object instance = BeanUtils.instantiate(clazz);
			BeanWrapperImpl wrapper = new BeanWrapperImpl(instance);
			for (String key : scriptObject.getOwnKeys(true)) {
				PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(key);
				Type fieldType = descriptor.getReadMethod().getGenericReturnType();
				wrapper.setPropertyValue(key, convertScriptValue(fieldType, scriptObject.get(key)));
			}
			return instance;
		} else if (type instanceof ParameterizedType) {
			if (!(scriptValue instanceof ScriptObjectMirror)) {
				throw new ValidationError(name() + ": cannot convert object to " + type.toString(), null);
			}
			ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] args = paramType.getActualTypeArguments();
			if ((paramType.getRawType() != List.class) || (args.length != 1)) {
				throw new ValidationError(name() + ": cannot convert object to " + type.toString(), null);
			}
			Type listType = args[0];
			List<Object> scriptList = (List<Object>) ScriptUtils.convert(scriptObject, List.class);
			List<Object> list = new ArrayList<>();
			for (Object scriptListObject : scriptList) {
				list.add(convertScriptValue(listType, scriptListObject));
			}
			return list;
		} else {
			throw new ValidationError(name() + ": cannot convert object to " + type.toString(), null);
		}
	}

	public <T> T checkFieldValue(JsonDescriptor descriptor, String field, T value) throws ValidationError {
		if (value != null) {
			return value;
		} else {
			throw new ValidationError(name() + ": missing field \"" + field + "\" in descriptor", descriptor);
		}
	}

	public <T> T optionalFieldValue(JsonDescriptor descriptor, String field, T value, T defaultValue) throws ValidationError {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public long durationValue(JsonDescriptor jsonDescriptor, String fieldName, String delayString) {
		checkFieldValue(jsonDescriptor, fieldName, delayString);
		try {
			return Duration.parse(delayString).toMillis();
		} catch (DateTimeParseException e) {
			throw new ValidationError(name() + ":" + e.getLocalizedMessage() + " at index " + e.getErrorIndex() + ": \"" + delayString + "\"", jsonDescriptor);
		}
	}

	public long optionalDurationValue(JsonDescriptor jsonDescriptor, String fieldName, String delayString, long defaultValue) {
		if (delayString == null) {
			return defaultValue;
		} else {
			return durationValue(jsonDescriptor, fieldName, delayString);
		}
	}

	public String checkVariableSubstitution(JsonDescriptor descriptor, String field, String text) throws ValidationError {
		try {
			return ApplicationUtil.substituteVariables(text);
		} catch (UndefinedSubstitutionVariable e) {
			throw new ValidationError(name() + ": unknown variable in " + name() + " field \"" + field + "\": " + e.getName(), descriptor);
		}
	}

	public Pattern compilePattern(JsonDescriptor description, String field, String regex) throws ValidationError {
		try {
			return Pattern.compile(regex);
		} catch (PatternSyntaxException e) {
			throw new ValidationError(name() + ": invalid regex in field \"" + field + "\":" + e.getMessage(), description);
		}
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}
