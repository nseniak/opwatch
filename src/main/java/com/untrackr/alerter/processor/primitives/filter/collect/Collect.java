package com.untrackr.alerter.processor.primitives.filter.collect;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.processor.payload.ObjectSeries;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.payload.SeriesObject;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Collect extends Filter<CollectConfig> {

	private int count;
	private EvictingQueue<SeriesObject> queue;

	public Collect(ProcessorService processorService, CollectConfig configuration, String name, int count) {
		super(processorService, configuration, name);
		this.count = count;
		this.queue = EvictingQueue.create(count);
	}

	@Override
	public synchronized void consume(Payload<?> payload) {
		Object value = payload.getValue();
		if (value != null) {
			long timestamp = System.currentTimeMillis();
			queue.add(new SeriesObject(value, timestamp));
			if (queue.size() == count) {
				ObjectSeries list = new ObjectSeries(queue);
				outputTransformed(list, payload);
			}
		}
	}

}
