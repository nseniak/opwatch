package com.untrackr.alerter.processor.filter.collect;

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
	public Processor make(Object scriptObject) throws ValidationError {
		CollectDesc descriptor = convertProcessorArgument(CollectDesc.class, scriptObject);
		JavascriptTransformer transformer = optionalFieldValue("transformer", descriptor.getTransformer(), null);
		int count = checkFieldValue("count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), ScriptStack.currentStack(), transformer, count);
		initialize(collect, descriptor);
		return collect;
	}

}
