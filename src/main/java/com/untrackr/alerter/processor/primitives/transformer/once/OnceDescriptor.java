package com.untrackr.alerter.processor.primitives.transformer.once;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;

public class OnceDescriptor extends ActiveProcessorDescriptor {

	private String delay;

	@DefaultOption
	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

}
