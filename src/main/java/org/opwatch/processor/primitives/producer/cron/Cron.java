package org.opwatch.processor.primitives.producer.cron;

import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

public class Cron extends ScheduledProducer<CronConfig> {

	private CommandRunner commandRunner;

	public Cron(ProcessorService processorService, CronConfig configuration, String name, ScheduledExecutor scheduledExecutor, CommandRunner commandRunner) {
		super(processorService, configuration, name, scheduledExecutor);
		this.commandRunner = commandRunner;
	}

	@Override
	protected void produce() {
		commandRunner.startProcess(this);
		long exitTimeout = processorService.config().cronCommandExitTimeout();
		commandRunner.produce(this, exitTimeout);
	}

}
