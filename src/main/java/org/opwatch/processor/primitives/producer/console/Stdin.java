package org.opwatch.processor.primitives.producer.console;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.ConsoleService;
import org.opwatch.service.ProcessorService;

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
		Payload payload = Payload.makeRoot(processorService, this, line.getText());
		payload.setMetadata(new StdinPayloadMetadata(line.getLine()));
		output(payload);
	}

}
