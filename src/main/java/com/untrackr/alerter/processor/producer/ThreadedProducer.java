package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public abstract class ThreadedProducer extends Producer {

	private Future<?> future;

	public ThreadedProducer(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
	}

	@Override
	public void doStart() {
		future = processorService.getConsumerExecutor().submit(this::run);
	}

	@Override
	public void doStop() {
		boolean stopped = future.cancel(true);
		if (!stopped) {
			throw new RuntimeProcessorError("cannot stop producer thread", this, null);
		}
	}

	public abstract void run();

}
