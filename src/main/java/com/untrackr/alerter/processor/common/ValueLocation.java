package com.untrackr.alerter.processor.common;

import static com.untrackr.alerter.processor.common.ValueLocation.SourceType.property;

public class ValueLocation {

	public enum SourceType {
		property, list_property_element, argument, toplevel
	}

	private SourceType sourceType;
	private String processorName;
	private String propertyName;

	private ValueLocation(SourceType sourceType, String processorName, String propertyName) {
		this.sourceType = sourceType;
		this.processorName = processorName;
		this.propertyName = propertyName;
	}

	public static ValueLocation makeProperty(String processorName, String propertyName) {
		return new ValueLocation(property, processorName, propertyName);
	}

	public static ValueLocation makeListPropertyElement(String processorName, String propertyName) {
		return new ValueLocation(SourceType.list_property_element, processorName, propertyName);
	}

	public static ValueLocation makeToplevel() {
		return new ValueLocation(SourceType.toplevel, null, null);
	}

	public static ValueLocation makeArgument(String processorName) {
		return new ValueLocation(SourceType.argument, processorName, null);
	}

	public ValueLocation toListElement() {
		if (sourceType == SourceType.property) {
			return makeListPropertyElement(processorName, propertyName);
		} else {
			return makeToplevel();
		}
	}

	public String describeAsLocation() {
		switch (sourceType) {
			case property:
			case list_property_element:
				return processorName + "." + propertyName;
			default:
				return null;
		}
	}

	public String describeAsValue() {
		switch (sourceType) {
			case property:
				return "\"" + propertyName + "\" value";
			case list_property_element:
				return "element in \"" + propertyName + "\" array";
			case argument:
				return "argument";
			default:
				return "object";
		}
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public String getProcessorName() {
		return processorName;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
}
