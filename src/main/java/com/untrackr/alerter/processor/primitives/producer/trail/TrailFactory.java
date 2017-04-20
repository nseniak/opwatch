package com.untrackr.alerter.processor.primitives.producer.trail;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TrailFactory extends ScheduledExecutorFactory<TrailConfig, Trail> {

	public TrailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trail";
	}

	@Override
	public Class<TrailConfig> configurationClass() {
		return TrailConfig.class;
	}

	@Override
	public Class<Trail> processorClass() {
		return Trail.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Trail make(Object scriptObject) {
		TrailConfig config = convertProcessorConfig(scriptObject);
		long duration = checkPropertyValue("duration", config.getDuration()).value(this);
		return new Trail(getProcessorService(), config, name(), makeScheduledExecutor(config), duration);
	}

}
