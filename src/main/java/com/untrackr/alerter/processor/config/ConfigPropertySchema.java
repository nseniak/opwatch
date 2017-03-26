package com.untrackr.alerter.processor.config;

public class ConfigPropertySchema {

	private String name;
	private boolean implicit;
	private boolean optional;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
