package org.opwatch.processor.primitives.producer.cron;

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class CronFactory extends ScheduledExecutorFactory<CronConfig, Cron> {

	public CronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "cron";
	}

	@Override
	public Class<CronConfig> configurationClass() {
		return CronConfig.class;
	}

	@Override
	public Class<Cron> processorClass() {
		return Cron.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Cron make(Object scriptObject) {
		CronConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config);
		CommandRunner runner = makeCommandOutputProducer(config);
		return new Cron(getProcessorService(), config, name(), executor, runner);
	}

}
