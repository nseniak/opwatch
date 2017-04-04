package com.untrackr.alerter.processor.primitives.producer.console;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
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
	public Class<StdinConfig> configurationClass() {
		return StdinConfig.class;
	}

	@Override
	public Class<Stdin> processorClass() {
		return Stdin.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Stdin make(Object scriptObject) {
		StdinConfig config = convertProcessorDescriptor(scriptObject);
		return new Stdin(getProcessorService(), config, name());
	}

}
