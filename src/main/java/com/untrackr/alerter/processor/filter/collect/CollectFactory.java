package com.untrackr.alerter.processor.filter.collect;

import com.untrackr.alerter.model.common.JsonDescriptor;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor scriptDescriptor = scriptDescriptor(object);
		CollectDesc descriptor = convertScriptDescriptor(CollectDesc.class, scriptDescriptor);
		JavascriptTransformer transformer = optionalFieldValue(scriptDescriptor, "transformer", descriptor.getTransformer(), null);
		int count = checkFieldValue(scriptDescriptor, "count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), ScriptStack.currentStack(), transformer, count);
		initialize(collect, descriptor);
		return collect;
	}

}
