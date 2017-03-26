package com.untrackr.alerter.processor.primitives.filter.collect;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.processor.payload.ObjectSeries;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.payload.SeriesObject;
import com.untrackr.alerter.processor.primitives.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Collect extends Filter<CollectConfig> {

	private int count;
	private EvictingQueue<Object> queue;

	public Collect(ProcessorService processorService, CollectConfig descriptor, String name, int count) {
		super(processorService, descriptor, name);
		this.count = count;
		this.queue = EvictingQueue.create(count);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		Object value = payload.getValue();
		if (value != null) {
			queue.add(value);
			if (queue.size() == count) {
				ObjectSeries list = new ObjectSeries();
				int i = 0;
				for (Object object : queue) {
					list.add(new SeriesObject(object, i));
					i = i + 1;
				}
				outputTransformed(list, payload);
			}
		}
	}

}
