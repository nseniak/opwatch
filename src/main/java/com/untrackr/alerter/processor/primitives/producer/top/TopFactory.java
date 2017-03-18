package com.untrackr.alerter.processor.primitives.producer.top;

import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TopFactory extends ScheduledExecutorFactory<TopDescriptor, Top> {

	public TopFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "top";
	}

	@Override
	public Class<TopDescriptor> descriptorClass() {
		return TopDescriptor.class;
	}

	@Override
	public Top make(Object scriptObject) {
		TopDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		Top top = new Top(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor));
		return top;
	}

}
