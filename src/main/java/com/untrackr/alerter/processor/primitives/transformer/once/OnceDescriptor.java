package com.untrackr.alerter.processor.primitives.transformer.once;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;

public class OnceDescriptor extends ActiveProcessorDescriptor {

	private String delay;

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

}
