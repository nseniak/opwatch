package com.untrackr.alerter.processor.primitives.producer.tail;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

public class TailDescriptor extends ActiveProcessorDescriptor {

	private String file;
	private Boolean ignoreBlankLine;

	@DefaultOption
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
