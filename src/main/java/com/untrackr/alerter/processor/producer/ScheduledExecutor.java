package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.TimeUnit;

public class ScheduledExecutor {

	private ProcessorService processorService;
	private long period;

	public ScheduledExecutor(ProcessorService processorService, long period) {
		this.processorService = processorService;
		this.period = period;
	}

	public void schedule(Runnable command) {
		processorService.getScheduledExecutor().scheduleAtFixedRate(command, 0, period, TimeUnit.MILLISECONDS);
	}

}
