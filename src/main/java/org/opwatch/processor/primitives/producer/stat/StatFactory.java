package org.opwatch.processor.primitives.producer.stat;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory<StatConfig, Stat> {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stat";
	}

	@Override
	public Class<StatConfig> configurationClass() {
		return StatConfig.class;
	}

	@Override
	public Class<Stat> processorClass() {
		return Stat.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Stat make(Object scriptObject) {
		StatConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		return new Stat(getProcessorService(), config, name(), makeScheduledExecutor(config), new java.io.File(file));
	}

}
