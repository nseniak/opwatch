package org.opwatch.processor.primitives.producer;

import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public abstract class Producer<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	public Producer(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload<?> payload) {
		throw new RuntimeError("producer should not receive input", new ProcessorVoidExecutionScope(this));
	}

}
