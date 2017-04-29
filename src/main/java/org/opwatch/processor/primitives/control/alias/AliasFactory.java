package org.opwatch.processor.primitives.control.alias;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.service.ProcessorService;

public class AliasFactory extends ProcessorFactory<AliasConfig, Alias> {

	public AliasFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alias";
	}

	@Override
	public Class<AliasConfig> configurationClass() {
		return AliasConfig.class;
	}

	@Override
	public Class<Alias> processorClass() {
		return Alias.class;
	}

	@Override
	public Alias make(Object scriptObject) {
		AliasConfig config = convertProcessorConfig(scriptObject);
		Processor processor = checkPropertyValue("processor", config.getProcessor());
		String name = checkPropertyValue("name", config.getName());
		return new Alias(getProcessorService(), processor, config, name);
	}

}
