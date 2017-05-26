package org.opwatch.processor.primitives.producer.cron;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class ShFactory extends ScheduledExecutorFactory<ShConfig, Sh> {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Class<ShConfig> configurationClass() {
		return ShConfig.class;
	}

	@Override
	public Class<Sh> processorClass() {
		return Sh.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Sh make(Object scriptObject) {
		ShConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config);
		CommandRunner runner = makeCommandOutputProducer(config);
		return new Sh(getProcessorService(), config, name(), executor, runner);
	}

}
