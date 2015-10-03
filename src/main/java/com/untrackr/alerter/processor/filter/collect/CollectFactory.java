package com.untrackr.alerter.processor.filter.collect;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.CompiledScript;

public class CollectFactory extends ActiveProcessorFactory {

	public CollectFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "collect";
	}

	@Override
	public Collect make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		CollectDesc descriptor = convertDescriptor(path, CollectDesc.class, jsonDescriptor);
		String valueSource = optionalFieldValue(path, jsonDescriptor, "value", descriptor.getValue(), null);
		CompiledScript valueScript = (valueSource == null) ? null : compileScript(path, jsonDescriptor, "value", valueSource);
		int count = checkFieldValue(path, jsonDescriptor, "count", descriptor.getCount());
		Collect collect = new Collect(getProcessorService(), path, valueSource, valueScript, count);
		initialize(collect, descriptor);
		return collect;
	}

}
