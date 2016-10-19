package com.untrackr.alerter.processor.producer.jscron.cron;

import com.untrackr.alerter.processor.common.JavascriptProducer;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class JSCron extends ScheduledProducer {

	private JavascriptProducer producer;

	public JSCron(ProcessorService processorService, String name, ScheduledExecutor scheduledExecutor, JavascriptProducer producer) {
		super(processorService, name, scheduledExecutor);
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
