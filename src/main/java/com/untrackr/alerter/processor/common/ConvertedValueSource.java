package com.untrackr.alerter.processor.common;

import static com.untrackr.alerter.processor.common.ConvertedValueSource.SourceType.property;

public class ConvertedValueSource {

	public enum SourceType {
		property, list_property_element, argument, toplevel
	}

	private SourceType sourceType;
	private String propertyName;

	public ConvertedValueSource(SourceType sourceType, String propertyName) {
		this.sourceType = sourceType;
		this.propertyName = propertyName;
	}

	public static ConvertedValueSource makeField(String fieldName) {
		return new ConvertedValueSource(property, fieldName);
	}

	public static ConvertedValueSource makeFieldListElement(String fieldName) {
		return new ConvertedValueSource(SourceType.list_property_element, fieldName);
	}

	public static ConvertedValueSource makeToplevel() {
		return new ConvertedValueSource(SourceType.toplevel, null);
	}

	public static ConvertedValueSource makeArgument() {
		return new ConvertedValueSource(SourceType.argument, null);
	}

	public ConvertedValueSource toListElement() {
		if (sourceType == SourceType.property) {
			return makeFieldListElement(propertyName);
		} else {
			return makeToplevel();
		}
	}

	public String describe() {
		switch (sourceType) {
			case property:
				return "\"" + propertyName + "\" value";
			case list_property_element:
				return "element in \"" + propertyName + "\"";
			case argument:
				return "argument";
			default:
				return "object";
		}
	}

}
