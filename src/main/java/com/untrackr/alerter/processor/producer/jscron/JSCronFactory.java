package com.untrackr.alerter.processor.producer.jscron;

import com.untrackr.alerter.processor.common.JavascriptProducer;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JSCronFactory extends ScheduledExecutorFactory {

	public JSCronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jscron";
	}

	@Override
	public JSCron make(Object scriptObject) {
		JSCronDesc descriptor = convertProcessorArgument(JSCronDesc.class, scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptProducer producer = checkPropertyValue("producer", descriptor.getProducer());
		JSCron JSCron = new JSCron(getProcessorService(), displayName(descriptor), executor, producer);
		return JSCron;
	}

}
