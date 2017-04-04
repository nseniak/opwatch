package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.OptionalProperty;

public class TraceConfig extends ActiveProcessorConfig {

	private Boolean payload = false;

	@OptionalProperty
	@ImplicitProperty
	public Boolean getPayload() {
		return payload;
	}

	public void setPayload(Boolean payload) {
		this.payload = payload;
	}

}
