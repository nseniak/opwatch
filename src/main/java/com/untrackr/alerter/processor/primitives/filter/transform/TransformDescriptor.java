package com.untrackr.alerter.processor.primitives.filter.transform;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;

public class TransformDescriptor extends ActiveProcessorDescriptor {

	private JavascriptFilter transformer;

	@DefaultOption
	public JavascriptFilter getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptFilter transformer) {
		this.transformer = transformer;
	}

}
