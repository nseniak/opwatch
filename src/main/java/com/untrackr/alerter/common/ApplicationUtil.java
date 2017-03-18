package com.untrackr.alerter.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ApplicationUtil {

	public static void checkEnvironmentVariable(String name) {
		if (System.getenv(name) == null) {
			throw new IllegalStateException("Environment variable not defined: " + name);
		}
	}

	public static String environmentVariable(String name) {
		checkEnvironmentVariable(name);
		return System.getenv(name);
	}

	public static <T> T  jsonEnvironmentVariable(String name, Class<T> clazz) throws IOException {
		String jsonString = environmentVariable(name);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, clazz);
	}

	public static String environmentVariable(String name, String defaultValue) {
		String value = System.getenv(name);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static void checkProperty(String name) {
		if (System.getProperty(name) == null) {
			throw new IllegalStateException("Property not defined: -D" + name);
		}
	}

	public static String property(String name) {
		checkProperty(name);
		return System.getProperty(name);
	}

	public static <T> T objectProperty(String name, Class<T> clazz) throws IOException {
		String jsonString = property(name);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, clazz);
	}

	public static String property(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static int property(String name, int defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static long property(String name, long defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value);
		}
	}

	public static boolean property(String name, boolean defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	private static Pattern variablePattern = Pattern.compile("\\$\\{([A-Za-z0-9_\\.]+)\\}");

	public static String substituteVariables(String text) throws UndefinedSubstitutionVariableException {
		Matcher matcher = variablePattern.matcher(text);
		StringBuffer stringBuffer = new StringBuffer();
		while (matcher.find()) {
			String value = variableValue(matcher.group(1));
			matcher.appendReplacement(stringBuffer, value);
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	private static String variableValue(String name) throws UndefinedSubstitutionVariableException {
		String propertyValue = System.getProperty(name);
		if (propertyValue != null) {
			return propertyValue;
		}
		String envValue = System.getenv(name);
		if (envValue != null) {
			return envValue;
		}
		throw new UndefinedSubstitutionVariableException(name);
	}

}
