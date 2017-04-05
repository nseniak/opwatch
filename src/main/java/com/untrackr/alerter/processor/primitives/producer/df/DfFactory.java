package com.untrackr.alerter.processor.primitives.producer.df;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

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
		String file = checkVariableSubstitution("file", checkPropertyValue("file", config.getFile()));
		return new Df(getProcessorService(), config, name(), makeScheduledExecutor(config), new java.io.File(file));
	}

}
