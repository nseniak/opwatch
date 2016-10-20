package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.SeriesObject;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class TrailCollecter extends ScheduledProducer {

	private long duration;
	protected LinkedBlockingQueue<SeriesObject> queue;
	private long startupTimestamp;

	public TrailCollecter(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor, long duration) {
		super(processorService, name, scheduledExecutor);
		this.duration = duration;
		this.queue = new LinkedBlockingQueue<>();
		this.signature = ProcessorSignature.makeTransformer();
	}

	@Override
	public void doStart() {
		createConsumerThread();
		startupTimestamp = System.currentTimeMillis();
		super.doStart();
	}

	@Override
	public void doStop() {
		stopConsumerThread();
	}

	@Override
	public void consume(Payload payload) {
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
