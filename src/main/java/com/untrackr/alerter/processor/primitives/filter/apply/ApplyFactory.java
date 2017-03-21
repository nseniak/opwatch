package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.service.ProcessorService;

public class ApplyFactory extends ActiveProcessorFactory<ApplyDescriptor, Apply> {

	public ApplyFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "transform";
	}

	@Override
	public Class<ApplyDescriptor> descriptorClass() {
		return ApplyDescriptor.class;
	}

	@Override
	public Apply make(Object scriptObject) {
		ApplyDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptFilter transformer = checkPropertyValue("transformer", descriptor.getLambda());
		Apply apply = new Apply(getProcessorService(), descriptor, name(), transformer);
		return apply;
	}

}
