package com.untrackr.alerter.processor.primitives.producer.top;

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
	public Top make(Object scriptObject) {
		TopConfig descriptor = convertProcessorDescriptor(scriptObject);
		Top top = new Top(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor));
		return top;
	}

}
