package org.opwatch.processor.primitives.producer.console;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

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
		StdinConfig config = convertProcessorConfig(scriptObject);
		return new Stdin(getProcessorService(), config, name());
	}

}