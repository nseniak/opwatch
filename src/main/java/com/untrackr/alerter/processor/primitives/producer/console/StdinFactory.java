package com.untrackr.alerter.processor.primitives.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StdinFactory extends ActiveProcessorFactory<StdinConfig, Stdin> {

	public StdinFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stdin";
	}

	@Override
	public Class<StdinConfig> descriptorClass() {
		return StdinConfig.class;
	}

	@Override
	public Stdin make(Object scriptObject) {
		StdinConfig descriptor = convertProcessorDescriptor(scriptObject);
		Stdin stdin = new Stdin(getProcessorService(), descriptor, name());
		return stdin;
	}

}
