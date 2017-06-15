package org.opwatch.processor.primitives.producer.sh;

import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

public class Sh extends ScheduledProducer<ShConfig> {

	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ShConfig configuration, String name, ScheduledExecutor scheduledExecutor, CommandRunner commandRunner) {
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
