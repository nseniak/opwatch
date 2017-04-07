package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.service.ProcessorService;

public abstract class ExecutionScope {

	public abstract MessageContext makeContext(ProcessorService processorService, ScriptStack stack);

}
