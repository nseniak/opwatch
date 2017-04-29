package org.opwatch.processor.primitives.producer.repeat;

import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

public class Repeat extends ScheduledProducer<RepeatConfig> {

	private JavascriptProducer producer;

	public Repeat(ProcessorService processorService, RepeatConfig configuration, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
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
