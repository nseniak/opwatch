package org.opwatch.processor.primitives.producer;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutor {

	private ProcessorService processorService;
	private long period;
	private ScheduledFuture<?> scheduledFuture;

	public ScheduledExecutor(ProcessorService processorService, long period) {
		this.processorService = processorService;
		this.period = period;
	}

	public void schedule(Runnable command) {
		scheduledFuture = processorService.getScheduledExecutor().scheduleAtFixedRate(command, 0, period, TimeUnit.MILLISECONDS);
	}

	public void stop(Processor<?> processor) {
		boolean stopped = scheduledFuture.cancel(true);
		if (!stopped) {
			throw new RuntimeError("couldn't stop scheduled producer", new ProcessorVoidExecutionScope(processor));
		}
	}

	public boolean running() {
		return !scheduledFuture.isCancelled();
	}

}
