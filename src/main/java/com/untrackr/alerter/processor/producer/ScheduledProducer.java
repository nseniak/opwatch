package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.service.ProcessorService;

public abstract class ScheduledProducer extends Producer {

	private ScheduledExecutor scheduledExecutor;
	private int count = 0;

	public ScheduledProducer(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor) {
		super(processorService, path);
		this.scheduledExecutor = scheduledExecutor;
	}

	@Override
	public void doStart() {
		scheduledExecutor.schedule(() -> {
			processorService.withErrorHandling(this, null, () -> {
				Object object = produce();
				if (object != null) {
					outputProduced(object);
					count = count + 1;
				}
			});
		});
	}

	@Override
	public void doStop() {
		scheduledExecutor.stop(this);
	}

	protected abstract Object produce();

	@Override
	public void consume(Payload payload) {
		// Do nothing
	}

}
