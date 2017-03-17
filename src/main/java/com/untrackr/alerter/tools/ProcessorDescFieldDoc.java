package com.untrackr.alerter.tools;

public class ProcessorDescFieldDoc {

	private String name;
	private String typeName;
	private String defaultValue;
	private String documentation;

	public ProcessorDescFieldDoc(String name, String typeName, String defaultValue, String documentation) {
		this.name = name;
		this.typeName = typeName;
		this.defaultValue = defaultValue;
		this.documentation = documentation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

}
