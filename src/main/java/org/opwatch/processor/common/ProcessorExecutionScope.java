package org.opwatch.processor.common;

import org.opwatch.service.ProcessorService;

public abstract class ProcessorExecutionScope extends ExecutionScope {

	private Processor<?> processor;

	protected ProcessorExecutionScope(Processor<?> processor) {
		this.processor = processor;
	}

	@Override
	public MessageContext makeContext(ProcessorService processorService, ScriptStack stack) {
		return MessageContext.makeProcessor(processorService, processor.getName(), null, stack);
	}

	public Processor<?> getProcessor() {
		return processor;
	}

}