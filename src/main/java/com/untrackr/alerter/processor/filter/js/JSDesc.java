package com.untrackr.alerter.processor.filter.js;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.JavascriptTransformer;

public class JSDesc extends ActiveProcessorDesc {

	private JavascriptTransformer transformer;

	public JavascriptTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptTransformer transformer) {
		this.transformer = transformer;
	}

}
