package com.untrackr.alerter.processor.transformer.collect;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

public class CollectFactory extends ActiveProcessorFactory {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "collect";
	}

	@Override
	public Processor make(Object scriptObject) {
		CollectDesc descriptor = convertProcessorArgument(CollectDesc.class, scriptObject);
		JavascriptTransformer transformer = optionaPropertyValue("transformer", descriptor.getTransformer(), null);
		int count = checkPropertyValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), displayName(descriptor), transformer, count);
		return collect;
	}

}
