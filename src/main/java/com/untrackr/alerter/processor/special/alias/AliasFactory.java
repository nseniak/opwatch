package com.untrackr.alerter.processor.special.alias;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class AliasFactory extends ProcessorFactory<AliasDesc, Alias> {

	public AliasFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "alias";
	}

	@Override
	public Class<AliasDesc> descriptorClass() {
		return AliasDesc.class;
	}

	@Override
	public Alias make(Object scriptObject) {
		AliasDesc descriptor = convertProcessorDescriptor(scriptObject);
		String name = checkPropertyValue("name", descriptor.getName());
		Processor processor = checkPropertyValue("processor", descriptor.getProcessor());
		return new Alias(getProcessorService(), processor, descriptor, type());
	}

}
