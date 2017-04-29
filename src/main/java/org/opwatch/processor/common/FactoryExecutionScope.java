package org.opwatch.processor.common;

import org.opwatch.service.ProcessorService;

public class FactoryExecutionScope extends ExecutionScope {

	private ProcessorFactory<?, ?> factory;

	public FactoryExecutionScope(ProcessorFactory<?, ?> factory) {
		this.factory = factory;
	}

	@Override
	public MessageContext makeContext(ProcessorService processorService, ScriptStack stack) {
		return MessageContext.makeFactory(processorService, factory.name(), stack);
	}

	public ProcessorFactory<?, ?> getFactory() {
		return factory;
	}

}
