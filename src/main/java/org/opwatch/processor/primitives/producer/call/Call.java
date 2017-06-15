package org.opwatch.processor.primitives.producer.call;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptConsumer;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

public class Call extends ScheduledProducer<CallConfig> {

	private JavascriptConsumer input;
	private JavascriptProducer output;

	public Call(ProcessorService processorService, CallConfig configuration, String name, ScheduledExecutor scheduledExecutor,
							JavascriptConsumer input, JavascriptProducer output) {
		super(processorService, configuration, name, scheduledExecutor);
		this.input = input;
		this.output = output;
	}

	@Override
	public void inferSignature() {
		if (input == null) {
			signature = ProcessorSignature.makeProducer();
		} else {
			signature = ProcessorSignature.makeFilter();
		}
	}

	@Override
	public void consume(Payload payload) {
		input.call(payload, this);
	}

	@Override
	protected void produce() {
		Object result = output.call(this);
		if (result != ScriptRuntime.UNDEFINED) {
			outputProduced(result);
		}
	}

}
