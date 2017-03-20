package com.untrackr.alerter.processor.primitives.producer.jscron;

import com.untrackr.alerter.processor.descriptor.JavascriptProducer;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JSCronFactory extends ScheduledExecutorFactory<RepeatDescriptor, Repeat> {

	public JSCronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "repeat";
	}

	@Override
	public Class<RepeatDescriptor> descriptorClass() {
		return RepeatDescriptor.class;
	}

	@Override
	public Repeat make(Object scriptObject) {
		RepeatDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptProducer producer = checkPropertyValue("producer", descriptor.getProducer());
		return new Repeat(getProcessorService(), descriptor, name(), executor, producer);
	}

}
