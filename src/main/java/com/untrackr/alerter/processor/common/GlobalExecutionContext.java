package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public class GlobalExecutionContext extends ExecutionContext {

	public GlobalExecutionContext() {
	}

	@Override
	public MessageScope makeMessageScope(ProcessorService processorService) {
		return new MessageScope(processorService.getId(), null, MessageScope.Kind.global, processorService.config().hostName());
	}

	@Override
	public String emitterName() {
		return null;
	}

	@Override
	public void addContextData(MessageData data, ProcessorService processorService) {
		// Nothing to add
	}

}
