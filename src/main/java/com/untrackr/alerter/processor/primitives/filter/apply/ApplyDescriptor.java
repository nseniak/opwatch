package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;

public class ApplyDescriptor extends ActiveProcessorDescriptor {

	private JavascriptFilter lambda;

	@DefaultOption
	public JavascriptFilter getLambda() {
		return lambda;
	}

	public void setLambda(JavascriptFilter lambda) {
		this.lambda = lambda;
	}

}
