package org.opwatch.processor.primitives.filter;

import org.opwatch.processor.common.ActiveProcessor;
import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.service.ProcessorService;

public abstract class Filter<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	public Filter(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

}
