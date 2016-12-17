package com.untrackr.alerter.processor.producer.jscron;

import com.untrackr.alerter.processor.common.JavascriptProducer;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class JSCron extends ScheduledProducer {

	private JavascriptProducer producer;

	public JSCron(ProcessorService processorService, JSCronDesc descriptor, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
		super(processorService, descriptor, name, scheduledExecutor);
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
