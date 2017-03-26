package com.untrackr.alerter.processor.config;

import java.util.List;

public class ProcessorSchema {

	private String name;
	private List<ConfigPropertySchema> properties;
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ConfigPropertySchema> getProperties() {
		return properties;
	}

	public void setProperties(List<ConfigPropertySchema> properties) {
		this.properties = properties;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
