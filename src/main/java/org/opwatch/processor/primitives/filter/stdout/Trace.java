package org.opwatch.processor.primitives.filter.stdout;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;

public class Trace extends Consumer<TraceConfig> {

	public Trace(ProcessorService processorService, TraceConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload payload) {
		ScriptService scriptService = processorService.getScriptService();
		processorService.printStdout(scriptService.pretty(payload.getValue()));
		outputTransformed(payload.getValue(), payload);
	}

}
