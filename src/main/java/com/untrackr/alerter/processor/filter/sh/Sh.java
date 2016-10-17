package com.untrackr.alerter.processor.filter.sh;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public class Sh extends Filter {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Sh(ProcessorService processorService, ScriptStack stack, CommandRunner commandRunner) {
		super(processorService, stack);
		this.commandRunner = commandRunner;
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.any, ProcessorSignature.PipeRequirement.any);
	}

	@Override
	public void doStart() {
		super.doStart();
		commandConsumerThreadFuture = processorService.getConsumerExecutor().submit(() -> {
			processorService.withErrorHandling(this, null, () -> {
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

	@Override
	public String identifier() {
		return commandRunner.getCommand();
	}

}
