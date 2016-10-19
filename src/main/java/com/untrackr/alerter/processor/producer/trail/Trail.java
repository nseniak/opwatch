package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.common.SeriesObject;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.util.concurrent.LinkedBlockingQueue;

public class Trail extends ScheduledProducer {

	private JavascriptTransformer transformer;
	private long duration;
	private LinkedBlockingQueue<SeriesObject> queue;
	private long startupTimestamp;

	public Trail(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor, JavascriptTransformer transformer, long duration) {
		super(processorService, name, scheduledExecutor);
		this.transformer = transformer;
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
		Object result = (transformer == null) ? payload.getScriptObject() : transformer.call(payload, this);
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
		outputProduced(new ObjectSeries(queue));
	}

}
