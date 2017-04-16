package com.untrackr.alerter.processor.primitives.filter.stdout;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class Trace extends Consumer<TraceConfig> {

	public Trace(ProcessorService processorService, TraceConfig descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		processorService.printStdout(processorService.json(payload));
		outputTransformed(payload.getValue(), payload);
	}

}
