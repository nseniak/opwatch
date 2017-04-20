package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.payload.SeriesObject;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class TrailCollecter<D extends ScheduledProcessorConfig> extends ScheduledProcessor<D> {

	private long duration;
	protected LinkedBlockingQueue<SeriesObject> queue;
	private long startupTimestamp;

	public TrailCollecter(ProcessorService processorService, D configuration, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, configuration, name, scheduledExecutor);
		this.duration = duration;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void start() {
		startupTimestamp = System.currentTimeMillis();
		super.start();
	}

	@Override
	public void consume(Payload<?> payload) {
		long timestamp = System.currentTimeMillis();
		Object result = collectedObject(payload);
		if (result != null) {
			queue.add(new SeriesObject(result, timestamp));
		}
	}

	@Override
	protected void produce() {
		long timestamp = System.currentTimeMillis();
		if ((timestamp - startupTimestamp) < duration) {
			return;
		}
		queue.removeIf(to -> (timestamp - to.getTimestamp()) > duration);
		outputProduced(producedObject());
	}

	protected abstract Object collectedObject(Payload payload);

	protected abstract Object producedObject();

}
