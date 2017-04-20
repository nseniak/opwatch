package com.untrackr.alerter.processor.primitives.consumer.stdout;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;

public class Stdout extends Consumer<StdoutConfig> {

	public Stdout(ProcessorService processorService, StdoutConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload<?> payload) {
		System.out.println(processorService.json(payload.getValue()));
	}

}
