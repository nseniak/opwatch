package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class TailDesc extends ActiveProcessorDesc {

	private String file;
	private Boolean json;
	private Boolean ignoreBlankLine;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Boolean getJson() {
		return json;
	}

	public void setJson(Boolean json) {
		this.json = json;
	}

	public Boolean getIgnoreBlankLine() {
		return ignoreBlankLine;
	}

	public void setIgnoreBlankLine(Boolean ignoreBlankLine) {
		this.ignoreBlankLine = ignoreBlankLine;
	}

}
