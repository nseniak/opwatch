package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.service.ProcessorService;

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
			throw new AlerterException("cannot stop scheduled producer", ExceptionContext.makeProcessorNoPayload(processor));
		}
	}



}
