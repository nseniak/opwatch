package org.opwatch.processor.primitives.producer.repeat;

import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

public class Call extends ScheduledProducer<CallConfig> {

	private JavascriptProducer producer;

	public Call(ProcessorService processorService, CallConfig configuration, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
		super(processorService, configuration, name, scheduledExecutor);
		this.producer = producer;
	}

	@Override
	protected void produce() {
		Object result = producer.call(this);
		if (result != null) {
			outputProduced(result);
		}
	}

}
