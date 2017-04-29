package org.opwatch.processor.primitives.filter.collect;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;

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
