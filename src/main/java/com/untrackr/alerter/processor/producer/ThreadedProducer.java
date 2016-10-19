package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.Future;

public abstract class ThreadedProducer extends Producer {

	private Future<?> future;

	public ThreadedProducer(ProcessorService processorService, String name) {
		super(processorService, name);
	}

	@Override
	public void doStart() {
		future = processorService.getConsumerExecutor().submit(this::run);
	}

	@Override
	public void doStop() {
		boolean stopped = future.cancel(true);
		if (!stopped) {
			throw new AlerterException("cannot stop producer thread", ExceptionContext.makeToplevel());
		}
	}

	public abstract void run();

}
