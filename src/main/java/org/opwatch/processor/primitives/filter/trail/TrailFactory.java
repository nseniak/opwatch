package org.opwatch.processor.primitives.filter.trail;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

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
		return new Trail(getProcessorService(), config, name(), makeScheduledExecutor(config, false), duration);
	}

}
