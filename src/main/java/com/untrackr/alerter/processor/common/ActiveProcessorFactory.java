package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.descriptor.ActiveProcessorDesc;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ActiveProcessorFactory extends ProcessorFactory {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected void initialize(ActiveProcessor processor, ActiveProcessorDesc processorDesc) {
		if (processorDesc.getName() != null) {
			processor.setName(processorDesc.getName());
		}
	}

}
