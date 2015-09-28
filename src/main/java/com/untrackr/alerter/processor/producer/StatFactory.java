package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.model.descriptor.StatDesc;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "stat";
	}

	@Override
	public Stat make(JsonObject jsonObject, IncludePath path) throws ValidationError {
		StatDesc descriptor = convertDescriptor(path, StatDesc.class, jsonObject);
		String file = checkVariableSubstitution(path, jsonObject, "file", fieldValue(path, jsonObject, "file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), path, makeScheduledExecutor(descriptor), new java.io.File(file));
		initialize(stat, descriptor);
		return stat;
	}

}
