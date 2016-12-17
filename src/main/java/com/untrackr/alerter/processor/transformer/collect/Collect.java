package com.untrackr.alerter.processor.transformer.collect;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.common.SeriesObject;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorDesc;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Collect extends Transformer {

	private JavascriptTransformer transformer;
	private int count;
	private EvictingQueue<Object> queue;

	public Collect(ProcessorService processorService, ProcessorDesc descriptor, String name, JavascriptTransformer transformer, int count) {
		super(processorService, descriptor, name);
		this.transformer = transformer;
		this.count = count;
		this.queue = EvictingQueue.create(count);
	}

	@Override
	public void consume(Payload payload) {
		long timestamp = System.currentTimeMillis();
		Object result = (transformer == null) ? payload.getValue() : transformer.call(payload, this);
		if (result != null) {
			queue.add(result);
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
