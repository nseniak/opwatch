package com.untrackr.alerter.processor.primitives.producer.jscron;

import com.untrackr.alerter.processor.descriptor.JavascriptProducer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JSCronFactory extends ScheduledExecutorFactory<JSCronDescriptor, JSCron> {

	public JSCronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "jscron";
	}

	@Override
	public Class<JSCronDescriptor> descriptorClass() {
		return JSCronDescriptor.class;
	}

	@Override
	public JSCron make(Object scriptObject) {
		JSCronDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptProducer producer = checkPropertyValue("producer", descriptor.getProducer());
		JSCron JSCron = new JSCron(getProcessorService(), descriptor, type(), executor, producer);
		return JSCron;
	}

}
