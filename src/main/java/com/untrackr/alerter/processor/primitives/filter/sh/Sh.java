package com.untrackr.alerter.processor.primitives.filter.sh;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public class Sh extends Filter<ShConfig> {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ShConfig descriptor, String name, CommandRunner commandRunner) {
		super(processorService, descriptor, name);
		this.commandRunner = commandRunner;
	}

	@Override
	public void start() {
		super.start();
		commandConsumerThreadFuture = processorService.getConsumerExecutor().submit(() -> {
			processorService.withProcessorErrorHandling(this, () -> {
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
	public void consumeInOwnThread(Payload<?> payload) {
		commandRunner.consume(this, payload);
	}

}
