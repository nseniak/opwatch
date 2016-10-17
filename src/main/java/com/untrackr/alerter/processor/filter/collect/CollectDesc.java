package com.untrackr.alerter.processor.filter.collect;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;
import com.untrackr.alerter.processor.common.JavascriptTransformer;

public class CollectDesc extends ActiveProcessorDesc {

	private JavascriptTransformer transformer;
	private Integer count;

	public JavascriptTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(JavascriptTransformer transformer) {
		this.transformer = transformer;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
