package org.opwatch.processor.common;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.Future;

public abstract class ThreadedConsumer<D extends ActiveProcessorConfig> extends ActiveProcessor<D> {

	private Future<?> consumerThreadFuture;
	private ConsumerThreadRunner consumerThreadRunner;

	public ThreadedConsumer(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void start() {
		consumerThreadRunner = new ConsumerThreadRunner(processorService, this);
		consumerThreadFuture = processorService.getConsumerExecutor().submit(consumerThreadRunner);
	}

	@Override
	public void stop() {
		if (!consumerThreadFuture.isDone()) {
			boolean stopped = consumerThreadFuture.cancel(true);
			if (!stopped) {
				throw new RuntimeError("cannot stop consumer thread", new ProcessorVoidExecutionScope(this));
			}
		}
	}

	@Override
	public void consume(Payload<?> payload) {
		consumerThreadRunner.consume(payload);
	}

	public abstract void consumeInOwnThread(Payload<?> payload);

}
