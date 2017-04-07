package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public class GlobalExecutionScope extends ExecutionScope {

	public GlobalExecutionScope() {
	}

	@Override
	public MessageContext makeContext(ProcessorService processorService, ScriptStack stack) {
		return MessageContext.makeGlobal(processorService, stack);
	}

}
