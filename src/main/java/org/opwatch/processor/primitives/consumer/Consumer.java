package org.opwatch.processor.primitives.consumer;

import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.service.ProcessorService;

public abstract class Consumer<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	public Consumer(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

}
