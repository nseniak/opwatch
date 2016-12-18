package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class StdoutDesc extends ActiveProcessorDesc {

	private Boolean payload;

	public Boolean getPayload() {
		return payload;
	}

	public void setPayload(Boolean payload) {
		this.payload = payload;
	}

}
