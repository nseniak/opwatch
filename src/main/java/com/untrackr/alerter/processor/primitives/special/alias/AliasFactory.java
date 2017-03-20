package com.untrackr.alerter.processor.primitives.special.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class AliasFactory extends ProcessorFactory<AliasDescriptor, Alias> {

	public AliasFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alias";
	}

	@Override
	public Class<AliasDescriptor> descriptorClass() {
		return AliasDescriptor.class;
	}

	@Override
	public Alias make(Object scriptObject) {
		AliasDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		Processor processor = checkPropertyValue("processor", descriptor.getProcessor());
		return new Alias(getProcessorService(), processor, descriptor, name());
	}

}
