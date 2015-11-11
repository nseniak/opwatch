package com.untrackr.alerter.common;

import com.untrackr.alerter.model.common.JsonMap;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScriptObject {

	private ProcessorService processorService;

	public ScriptObject(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public Object clone() {
		return processorService.getObjectMapper().convertValue(this, JsonMap.class);
	}

}
