package com.untrackr.alerter.processor.primitives.filter.collect;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;

public class CollectConfig extends ActiveProcessorConfig {

	private Integer count;

	@ImplicitProperty
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
