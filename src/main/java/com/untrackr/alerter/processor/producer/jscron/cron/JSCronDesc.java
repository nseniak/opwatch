package com.untrackr.alerter.processor.producer.jscron.cron;

import com.untrackr.alerter.processor.common.JavascriptGenerator;
import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class JSCronDesc extends ScheduledProducerDesc {

	private JavascriptGenerator generator;

	public JavascriptGenerator getGenerator() {
		return generator;
	}

	public void setGenerator(JavascriptGenerator generator) {
		this.generator = generator;
	}

}
