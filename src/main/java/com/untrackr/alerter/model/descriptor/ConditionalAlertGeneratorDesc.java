package com.untrackr.alerter.model.descriptor;

public class ConditionalAlertGeneratorDesc extends ActiveProcessorDesc {

	/**
	 * Priority (optional). Defaults to NORMAL.
	 */
	private String priority;
	/**
	 * Title. Defaults to NORMAL.
	 */
	private String title;

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
