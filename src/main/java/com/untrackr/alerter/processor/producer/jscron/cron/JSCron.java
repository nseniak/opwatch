package com.untrackr.alerter.processor.producer.jscron.cron;

import com.untrackr.alerter.processor.common.JavascriptGenerator;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

public class JSCron extends ScheduledProducer {

	private JavascriptGenerator generator;

	public JSCron(ProcessorService processorService, ScriptStack stack, ScheduledExecutor scheduledExecutor, JavascriptGenerator generator) {
		super(processorService, stack, scheduledExecutor);
		this.generator = generator;
	}

	@Override
	protected void produce() {
		Object result = generator.call(this);
		if (result != null) {
			outputProduced(result);
		}
	}

	@Override
	public String identifier() {
		return generator.toString();
	}

}
