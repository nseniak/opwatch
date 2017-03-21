package com.untrackr.alerter.processor.primitives.filter.collect;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.service.ProcessorService;

public class CollectFactory extends ActiveProcessorFactory<CollectDescriptor, Collect> {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "collect";
	}

	@Override
	public Class<CollectDescriptor> descriptorClass() {
		return CollectDescriptor.class;
	}

	@Override
	public Collect make(Object scriptObject) {
		CollectDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptFilter transformer = descriptor.getTransformer();
		int count = checkPropertyValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), descriptor, name(), transformer, count);
		return collect;
	}

}
