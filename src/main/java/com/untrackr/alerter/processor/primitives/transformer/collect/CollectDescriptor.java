package com.untrackr.alerter.processor.primitives.transformer.collect;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptTransformer;

public class CollectDescriptor extends ActiveProcessorDescriptor {

	private JavascriptTransformer transformer;
	private Integer count;

	public JavascriptTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptTransformer transformer) {
		this.transformer = transformer;
	}

	@DefaultOption
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
