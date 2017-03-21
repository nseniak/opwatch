package com.untrackr.alerter.processor.primitives.filter.collect;

import com.untrackr.alerter.processor.descriptor.ActiveProcessorDescriptor;
import com.untrackr.alerter.processor.descriptor.DefaultOption;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;

public class CollectDescriptor extends ActiveProcessorDescriptor {

	private JavascriptFilter transformer;
	private Integer count;

	public JavascriptFilter getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptFilter transformer) {
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
