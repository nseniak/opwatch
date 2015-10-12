package com.untrackr.alerter.processor.producer.trail;

import com.untrackr.alerter.common.SeriesObject;
import com.untrackr.alerter.common.ObjectSeries;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import javax.script.Bindings;
import javax.script.CompiledScript;
import java.util.concurrent.LinkedBlockingQueue;

public class Trail extends ScheduledProducer {

	private String source;
	private CompiledScript value;
	private long duration;
	private LinkedBlockingQueue<SeriesObject> queue;
	private Bindings bindings;
	private long startupTimestamp;

	public Trail(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor, String source, CompiledScript value, long duration) {
		super(processorService, path, scheduledExecutor);
		this.source = source;
		this.value = value;
		this.duration = duration;
		this.queue = new LinkedBlockingQueue<>();
		this.bindings = processorService.getNashorn().createBindings();
		this.signature = ProcessorSignature.makeFilter();
	}

	@Override
	public void doStart() {
		createConsumerThread();
		startupTimestamp = System.currentTimeMillis();
	}

	@Override
	public void doStop() {
		stopConsumerThread();
	}

	@Override
	public void consume(Payload payload) {
		long timestamp = System.currentTimeMillis();
		Object result = (value == null) ? payload.getJsonObject() : runScript(value, bindings, payload);
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

	@Override
	public String identifier() {
		return source;
	}

}
