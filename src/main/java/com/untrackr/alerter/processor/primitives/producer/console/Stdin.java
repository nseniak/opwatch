package com.untrackr.alerter.processor.primitives.producer.console;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.Producer;
import com.untrackr.alerter.service.ConsoleService;
import com.untrackr.alerter.service.ProcessorService;

public class Stdin extends Producer<StdinConfig> implements ConsoleService.ConsoleLineConsumer {

	public Stdin(ProcessorService processorService, StdinConfig descriptor, String name) {
		super(processorService, descriptor, name);
	}

	@Override
	public void start() {
		processorService.getConsoleService().addConsumer(this);
	}

	@Override
	public void stop() {
		processorService.getConsoleService().removeConsumer(this);
	}

	@Override
	public void consume(ConsoleService.ConsoleLine line) {
		Payload payload = new StdinPayload(System.currentTimeMillis(), processorService.config().hostName(), location, null,
				line.getText(), line.getLine());
		output(payload);
	}

}
