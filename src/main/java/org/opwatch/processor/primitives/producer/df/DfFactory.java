package org.opwatch.processor.primitives.producer.df;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory<DfConfig, Df> {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "df";
	}

	@Override
	public Class<DfConfig> configurationClass() {
		return DfConfig.class;
	}

	@Override
	public Class<Df> processorClass() {
		return Df.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Df make(Object scriptObject) {
		DfConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		return new Df(getProcessorService(), config, name(), makeScheduledExecutor(config), new java.io.File(file));
	}

}
