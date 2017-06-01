package org.opwatch.processor.primitives.filter.sh;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.Future;

public class Shapply extends Filter<ShapplyConfig> {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public Shapply(ProcessorService processorService, ShapplyConfig configuration, String name, CommandRunner commandRunner) {
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
	public void consume(Payload payload) {
		commandRunner.consume(this, payload);
	}

}
