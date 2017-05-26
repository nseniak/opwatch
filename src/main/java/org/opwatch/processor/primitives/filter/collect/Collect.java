package org.opwatch.processor.primitives.filter.collect;

import com.google.common.collect.EvictingQueue;
import org.opwatch.processor.payload.ObjectSeries;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.payload.SeriesObject;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

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
				SeriesObject[] objects = new SeriesObject[queue.size()];
				queue.toArray(objects);
				outputTransformed(ObjectSeries.toJavascript(processorService.getScriptService(), objects), payload);
			}
		}
	}

}
