package com.untrackr.alerter.processor.producer.top;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class TopFactory extends ScheduledExecutorFactory {

	public TopFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "top";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		TopDesc descriptor = convertScriptDescriptor(TopDesc.class, jsonDescriptor);
		Top top = new Top(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor));
		initialize(top, descriptor);
		return top;
	}

}
