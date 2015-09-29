package com.untrackr.alerter.processor.filter.once;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class OnceDesc extends ActiveProcessorDesc {

	private String delay;

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

}
