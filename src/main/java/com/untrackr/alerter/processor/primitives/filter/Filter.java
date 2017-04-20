package com.untrackr.alerter.processor.primitives.filter;

import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.config.ActiveProcessorConfig;
import com.untrackr.alerter.service.ProcessorService;

public abstract class Filter<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	public Filter(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

}
