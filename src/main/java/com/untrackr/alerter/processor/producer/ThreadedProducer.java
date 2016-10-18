package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ProcessorExecutionException;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public abstract class ThreadedProducer extends Producer {

	private Future<?> future;

	public ThreadedProducer(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
	}

	@Override
	public void doStart() {
		future = processorService.getConsumerExecutor().submit(this::run);
	}

	@Override
	public void doStop() {
		boolean stopped = future.cancel(true);
		if (!stopped) {
			throw new ProcessorExecutionException("cannot stop producer thread", this, null);
		}
	}

	public abstract void run();

}
