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

	public TrailCollecter(ProcessorService processorService, D descriptor, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, descriptor, name, scheduledExecutor);
		this.duration = duration;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void inferSignature() {
		this.signature = ProcessorSignature.makeTransformer();
	}

	@Override
	public void start() {
		createConsumerThread();
		startupTimestamp = System.currentTimeMillis();
		super.start();
	}

	@Override
	public void stop() {
		stopConsumerThread();
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
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
		queue.removeIf(to -> (timestamp - to.getStamp()) > duration);
		outputProduced(producedObject());
	}

	protected abstract Object collectedObject(Payload payload);

	protected abstract Object producedObject();

}
