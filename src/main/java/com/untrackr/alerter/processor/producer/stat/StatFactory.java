package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class StatFactory extends ScheduledExecutorFactory {

	public StatFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "stat";
	}

	@Override
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		StatDesc descriptor = convertScriptDescriptor(StatDesc.class, jsonDescriptor);
		String file = checkVariableSubstitution(jsonDescriptor, "file", checkFieldValue(jsonDescriptor, "file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor), new java.io.File(file));
		initialize(stat, descriptor);
		return stat;
	}

}
