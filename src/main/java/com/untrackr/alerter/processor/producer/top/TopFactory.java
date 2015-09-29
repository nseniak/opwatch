package com.untrackr.alerter.processor.producer.top;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Top make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		TopDesc descriptor = convertDescriptor(path, TopDesc.class, jsonObject);
		Top top = new Top(getProcessorService(), path, makeScheduledExecutor(descriptor));
		initialize(top, descriptor);
		return top;
	}

}
