package com.untrackr.alerter.processor.primitives.filter.collect;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CollectFactory extends ActiveProcessorFactory<CollectConfig, Collect> {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "collect";
	}

	@Override
	public Class<CollectConfig> configurationClass() {
		return CollectConfig.class;
	}

	@Override
	public Class<Collect> processorClass() {
		return Collect.class;
	}

	@Override
	public Collect make(Object scriptObject) {
		CollectConfig descriptor = convertProcessorDescriptor(scriptObject);
		int count = checkPropertyValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), descriptor, name(), count);
		return collect;
	}

}
