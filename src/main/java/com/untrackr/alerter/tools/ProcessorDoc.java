package com.untrackr.alerter.tools;

import java.util.List;

public class ProcessorDoc {

	private String type;
	private List<ProcessorDescFieldDoc> fields;
	private String documentation;

	public ProcessorDoc(String type, List<ProcessorDescFieldDoc> fields, String documentation) {
		this.type = type;
		this.fields = fields;
		this.documentation = documentation;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ProcessorDescFieldDoc> getFields() {
		return fields;
	}

	public void setFields(List<ProcessorDescFieldDoc> fields) {
		this.fields = fields;
	}

}
