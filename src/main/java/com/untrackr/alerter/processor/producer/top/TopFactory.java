package com.untrackr.alerter.processor.producer.top;

import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TopFactory extends ScheduledExecutorFactory {

	public TopFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "top";
	}

	@Override
	public Top make(Object scriptObject) {
		TopDesc descriptor = convertProcessorArgument(TopDesc.class, scriptObject);
		Top top = new Top(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor));
		return top;
	}

}
