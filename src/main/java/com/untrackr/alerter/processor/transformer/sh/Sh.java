package com.untrackr.alerter.processor.transformer.sh;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public class Sh extends Transformer {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ShDesc descriptor, String name, CommandRunner commandRunner) {
		super(processorService, descriptor, name);
		this.commandRunner = commandRunner;
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.any, ProcessorSignature.PipeRequirement.any);
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
	public void consume(Payload payload) {
		commandRunner.consume(this, payload);
	}

}
