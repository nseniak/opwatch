package org.opwatch.processor.primitives.producer;

import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.common.ScheduledProcessor;
import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public abstract class ScheduledProducer<D extends ScheduledProcessorConfig> extends ScheduledProcessor<D> {

	public ScheduledProducer(ProcessorService processorService, D configuration, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, configuration, name, scheduledExecutor);
	}

	@Override
	public void consume(Payload payload) {
		throw new RuntimeError("producer should not receive input", new ProcessorVoidExecutionScope(this));
	}

}
