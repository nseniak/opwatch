package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

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
	public Cron make(Object scriptObject) {
		CronConfig descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		CommandRunner runner = makeCommandOutputProducer(descriptor);
		Cron cron = new Cron(getProcessorService(), descriptor, name(), executor, runner);
		return cron;
	}

}
