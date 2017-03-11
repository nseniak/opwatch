package com.untrackr.alerter.processor.transformer.collect;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class CollectFactory extends ActiveProcessorFactory<CollectDesc, Collect> {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "collect";
	}

	@Override
	public Class<CollectDesc> descriptorClass() {
		return CollectDesc.class;
	}

	@Override
	public Collect make(Object scriptObject) {
		CollectDesc descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptTransformer transformer = optionaPropertyValue("transformer", descriptor.getTransformer(), null);
		int count = checkPropertyValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), descriptor, type(), transformer, count);
		return collect;
	}

}
