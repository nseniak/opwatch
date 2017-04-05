package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public class FactoryExecutionContext extends ExecutionContext {

	private ProcessorFactory<?, ?> factory;

	public FactoryExecutionContext(ProcessorFactory<?, ?> factory) {
		this.factory = factory;
	}

	@Override
	public MessageScope makeMessageScope(ProcessorService processorService) {
		return new MessageScope(processorService.getId(), factory.name(), MessageScope.Kind.factory, processorService.config().hostName());
	}

	@Override
	public String emitterName() {
		return factory.name();
	}

	@Override
	public void addContextData(MessageData data, ProcessorService processorService) {
		data.put("processor", factory.name());
	}

	public ProcessorFactory<?, ?> getFactory() {
		return factory;
	}

}
