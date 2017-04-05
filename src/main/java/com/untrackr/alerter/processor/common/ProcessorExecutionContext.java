package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public abstract class ProcessorExecutionContext extends ExecutionContext {

	private Processor<?> processor;

	public ProcessorExecutionContext(Processor<?> processor) {
		this.processor = processor;
	}

	@Override
	public MessageScope makeMessageScope(ProcessorService processorService) {
		return new MessageScope(processorService.getId(), processor.getName(), MessageScope.Kind.processor, processorService.config().hostName());
	}

	@Override
	public String emitterName() {
		return processor.getName();
	}

	@Override
	public void addContextData(MessageData data, ProcessorService processorService) {
		data.put("processor", processor.getLocation().descriptor());
	}

	public Processor<?> getProcessor() {
		return processor;
	}

}
