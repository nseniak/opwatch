package org.opwatch.processor.primitives.consumer.stdout;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

public class StdoutFactory extends ActiveProcessorFactory<StdoutConfig, Stdout> {

	public StdoutFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stdout";
	}

	@Override
	public Class<StdoutConfig> configurationClass() {
		return StdoutConfig.class;
	}

	@Override
	public Class<Stdout> processorClass() {
		return Stdout.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	@Override
	public Stdout make(Object scriptObject) {
		StdoutConfig config = convertProcessorConfig(scriptObject);
		return new Stdout(getProcessorService(), config, name());
	}

}
