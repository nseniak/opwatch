package com.untrackr.alerter.processor.producer.jscron;

import com.untrackr.alerter.processor.common.JavascriptProducer;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JSCronFactory extends ScheduledExecutorFactory<JSCronDesc, JSCron> {

	public JSCronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jscron";
	}

	@Override
	public Class<JSCronDesc> descriptorClass() {
		return JSCronDesc.class;
	}

	@Override
	public JSCron make(Object scriptObject) {
		JSCronDesc descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptProducer producer = checkPropertyValue("producer", descriptor.getProducer());
		JSCron JSCron = new JSCron(getProcessorService(), descriptor, type(), executor, producer);
		return JSCron;
	}

}
