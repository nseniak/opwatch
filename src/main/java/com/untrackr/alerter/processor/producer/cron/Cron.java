package com.untrackr.alerter.processor.producer.cron;

import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class Cron extends ScheduledProducer {

	private CommandRunner commandRunner;

	public Cron(ProcessorService processorService, CronDesc descriptor, String name, ScheduledExecutor scheduledExecutor, CommandRunner commandRunner) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.commandRunner = commandRunner;
	}

	@Override
	protected void produce() {
		commandRunner.startProcess(this);
		long exitTimeout = processorService.getProfileService().profile().getCronCommandExitTimeout();
		commandRunner.produce(this, exitTimeout);
	}

}
