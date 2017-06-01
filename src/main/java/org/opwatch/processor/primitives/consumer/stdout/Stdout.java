package org.opwatch.processor.primitives.consumer.stdout;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;

public class Stdout extends Consumer<StdoutConfig> {

	public Stdout(ProcessorService processorService, StdoutConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload payload) {
		processorService.printStdout(processorService.getScriptService().jsonStringify(payload.getValue()));
	}

}
