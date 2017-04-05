package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Cron extends ScheduledProducer<CronConfig> {

	private CommandRunner commandRunner;

	public Cron(ProcessorService processorService, CronConfig descriptor, String name, ScheduledExecutor scheduledExecutor, CommandRunner commandRunner) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.commandRunner = commandRunner;
	}

	@Override
	protected void produce() {
		commandRunner.startProcess(this);
		long exitTimeout = processorService.config().cronCommandExitTimeout();
		commandRunner.produce(this, exitTimeout);
	}

}
