package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.config.JavascriptFilter;
import com.untrackr.alerter.service.ProcessorService;

public class ApplyFactory extends ActiveProcessorFactory<ApplyConfig, Apply> {

	public ApplyFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "transform";
	}

	@Override
	public Class<ApplyConfig> configurationClass() {
		return ApplyConfig.class;
	}

	@Override
	public Apply make(Object scriptObject) {
		ApplyConfig descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptFilter transformer = checkPropertyValue("transformer", descriptor.getLambda());
		Apply apply = new Apply(getProcessorService(), descriptor, name(), transformer);
		return apply;
	}

}
