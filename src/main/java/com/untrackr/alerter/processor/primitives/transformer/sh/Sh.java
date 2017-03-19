package com.untrackr.alerter.processor.primitives.transformer.sh;

import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public class Sh extends Transformer<ShDescriptor> {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ShDescriptor descriptor, String name, CommandRunner commandRunner) {
		super(processorService, descriptor, name);
		this.commandRunner = commandRunner;
	}

	@Override
	public void inferSignature() {
		this.signature = ProcessorSignature.makeAny();
	}

	@Override
	public void doStart() {
		super.doStart();
		commandConsumerThreadFuture = processorService.getConsumerExecutor().submit(() -> {
			processorService.withProcessorErrorHandling(this, () -> {
				commandRunner.startProcess(this);
				commandRunner.produce(this, -1);
			});
		});
	}

	@Override
	public void doStop() {
		commandConsumerThreadFuture.cancel(true);
		commandRunner.stopProcess();
		super.doStop();
	}

	@Override
	public void doConsume(Payload<?> payload) {
		commandRunner.consume(this, payload);
	}

}
