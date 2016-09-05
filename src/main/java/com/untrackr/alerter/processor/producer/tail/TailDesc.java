package com.untrackr.alerter.processor.producer.tail;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class TailDesc extends ActiveProcessorDesc {

	private String file;
	private boolean ignoreBlankLine;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public boolean isIgnoreBlankLine() {
		return ignoreBlankLine;
	}

	public void setIgnoreBlankLine(boolean ignoreBlankLine) {
		this.ignoreBlankLine = ignoreBlankLine;
	}

}
