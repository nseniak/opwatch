package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class TailDescriptor extends ActiveProcessorDescriptor {

	private String file;
	private Boolean ignoreBlankLine;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Boolean getIgnoreBlankLine() {
		return ignoreBlankLine;
	}

	public void setIgnoreBlankLine(Boolean ignoreBlankLine) {
		this.ignoreBlankLine = ignoreBlankLine;
	}

}
