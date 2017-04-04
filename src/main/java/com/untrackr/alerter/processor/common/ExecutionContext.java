package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public abstract class ExecutionContext {

	public abstract MessageScope makeMessageScope(ProcessorService processorService);

	public abstract String emitterName();

	public abstract void addContextData(MessageData data, ProcessorService processorService);

}
