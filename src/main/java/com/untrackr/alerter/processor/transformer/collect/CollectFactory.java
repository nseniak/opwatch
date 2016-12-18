package com.untrackr.alerter.processor.transformer.collect;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class CollectFactory extends ActiveProcessorFactory {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "collect";
	}

	@Override
	public Collect make(Object scriptObject) {
		CollectDesc descriptor = convertProcessorDescriptor(CollectDesc.class, scriptObject);
		JavascriptTransformer transformer = optionaPropertyValue("transformer", descriptor.getTransformer(), null);
		int count = checkPropertyValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), descriptor, type(), transformer, count);
		return collect;
	}

}
