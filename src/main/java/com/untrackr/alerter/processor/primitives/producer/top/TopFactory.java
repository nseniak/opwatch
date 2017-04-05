package com.untrackr.alerter.processor.primitives.producer.top;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TopFactory extends ScheduledExecutorFactory<TopConfig, Top> {

	public TopFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "top";
	}

	@Override
	public Class<TopConfig> configurationClass() {
		return TopConfig.class;
	}

	@Override
	public Class<Top> processorClass() {
		return Top.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Top make(Object scriptObject) {
		TopConfig config = convertProcessorConfig(scriptObject);
		return new Top(getProcessorService(), config, name(), makeScheduledExecutor(config));
	}

}
