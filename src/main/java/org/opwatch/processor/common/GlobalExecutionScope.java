package org.opwatch.processor.common;

import org.opwatch.service.ProcessorService;

public class GlobalExecutionScope extends ExecutionScope {

	public GlobalExecutionScope() {
	}

	@Override
	public MessageContext makeContext(ProcessorService processorService, ScriptStack stack) {
		return MessageContext.makeGlobal(processorService, stack);
	}

}
