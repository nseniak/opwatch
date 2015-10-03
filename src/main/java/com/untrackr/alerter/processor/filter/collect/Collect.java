package com.untrackr.alerter.processor.filter.collect;

import com.google.common.collect.EvictingQueue;
import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.common.SeriesObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;

public class Collect extends Filter {

	private String source;
	private CompiledScript value;
	private int count;
	private EvictingQueue<Object> queue;
	private Bindings bindings;

	public Collect(ProcessorService processorService, IncludePath path, String source, CompiledScript value, int count) {
		super(processorService, path);
		this.source = source;
		this.value = value;
		this.count = count;
		this.queue = EvictingQueue.create(count);
		this.bindings = processorService.getNashorn().createBindings();
	}

	@Override
	public void consume(Payload payload) {
		long timestamp = System.currentTimeMillis();
		Object result = (value == null) ? payload.getJsonObject() : runScript(value, bindings, payload);
		if (result != null) {
			queue.add(result);
			if (queue.size() == count) {
				ObjectSeries list = new ObjectSeries();
				int i = 0;
				for (Object object : queue) {
					list.add(new SeriesObject(object, i));
					i = i + 1;
				}
				outputFiltered(list, payload);
			}
		}
	}

	@Override
	public String identifier() {
		return source;
	}

}
