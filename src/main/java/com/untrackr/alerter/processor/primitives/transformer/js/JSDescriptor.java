package com.untrackr.alerter.processor.primitives.transformer.js;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;

public class JSDescriptor extends ActiveProcessorDescriptor {

	private JavascriptTransformer transformer;

	public JavascriptTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptTransformer transformer) {
		this.transformer = transformer;
	}

}
