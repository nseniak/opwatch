package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
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
	public Stat make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		StatDesc descriptor = convertDescriptor(path, StatDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(path, jsonDescriptor, "file", checkFieldValue(path, jsonDescriptor, "file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), path, makeScheduledExecutor(path, jsonDescriptor, descriptor), new java.io.File(file));
		initialize(stat, descriptor);
		return stat;
	}

}
