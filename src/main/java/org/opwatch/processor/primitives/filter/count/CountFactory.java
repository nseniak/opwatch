package org.opwatch.processor.primitives.filter.count;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptPredicate;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class CountFactory extends ScheduledExecutorFactory<CountConfig, Count> {

	public CountFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "count";
	}

	@Override
	public Class<CountConfig> configurationClass() {
		return CountConfig.class;
	}

	@Override
	public Class<Count> processorClass() {
		return Count.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Count make(Object scriptObject) {
		CountConfig config = convertProcessorConfig(scriptObject);
		JavascriptPredicate predicate = config.getPredicate();
		long duration = checkPropertyValue("duration", config.getDuration()).value(this);
		return new Count(getProcessorService(), config, name(), makeScheduledExecutor(config), predicate, duration);
	}

}
