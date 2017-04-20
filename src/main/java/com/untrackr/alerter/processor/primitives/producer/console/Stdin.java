package com.untrackr.alerter.processor.primitives.producer.console;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.Producer;
import com.untrackr.alerter.service.ConsoleService;
import com.untrackr.alerter.service.ProcessorService;

public class Stdin extends Producer<StdinConfig> implements ConsoleService.ConsoleLineConsumer {

	public Stdin(ProcessorService processorService, StdinConfig configuration, String name) {
		super(processorService, configuration, name);
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
		Payload<String> payload = Payload.makeRoot(processorService, this, line.getText());
		payload.setMetadata(new StdinPayloadMetadata(line.getLine()));
		output(payload);
	}

}
