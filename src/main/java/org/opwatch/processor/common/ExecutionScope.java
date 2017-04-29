package org.opwatch.processor.common;

import org.opwatch.service.ProcessorService;

public abstract class ExecutionScope {

	public abstract MessageContext makeContext(ProcessorService processorService, ScriptStack stack);

}
