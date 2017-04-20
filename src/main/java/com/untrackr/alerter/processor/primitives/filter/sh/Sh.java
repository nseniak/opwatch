package com.untrackr.alerter.processor.primitives.filter.sh;

import com.untrackr.alerter.processor.common.ProcessorVoidExecutionScope;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public class Sh extends Filter<ShConfig> {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ShConfig configuration, String name, CommandRunner commandRunner) {
		super(processorService, configuration, name);
		this.commandRunner = commandRunner;
	}

	@Override
	public void start() {
		super.start();
		commandConsumerThreadFuture = processorService.getConsumerExecutor().submit(() -> {
			processorService.withExceptionHandling("error starting command",
					() -> new ProcessorVoidExecutionScope(this),
					() -> {
						commandRunner.startProcess(this);
						commandRunner.produce(this, -1);
					});
		});
	}

	@Override
	public void stop() {
		commandConsumerThreadFuture.cancel(true);
		commandRunner.stopProcess();
		super.stop();
	}

	@Override
	public void consume(Payload<?> payload) {
		commandRunner.consume(this, payload);
	}

}
