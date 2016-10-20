package com.untrackr.alerter.processor.producer.jscron;

import com.untrackr.alerter.processor.common.JavascriptProducer;
import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class JSCronDesc extends ScheduledProducerDesc {

	private JavascriptProducer producer;

	public JavascriptProducer getProducer() {
		return producer;
	}

	public void setProducer(JavascriptProducer producer) {
		this.producer = producer;
	}

}
