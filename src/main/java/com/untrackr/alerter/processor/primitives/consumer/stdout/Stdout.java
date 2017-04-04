package com.untrackr.alerter.processor.primitives.consumer.stdout;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class Stdout extends Consumer<StdoutConfig> {

	public Stdout(ProcessorService processorService, StdoutConfig descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		System.out.println(processorService.json(payload.getValue()));
	}

}
