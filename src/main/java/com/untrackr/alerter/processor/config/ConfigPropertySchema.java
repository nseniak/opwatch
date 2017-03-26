package com.untrackr.alerter.processor.config;

public class ConfigPropertySchema {

	private String name;
	private String type;
	private boolean implicit;
	private boolean optional;
	private Object defaultValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

}
