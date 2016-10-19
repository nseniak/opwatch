package com.untrackr.alerter.processor.producer.stat;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
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
	public Processor make(Object scriptObject) {
		StatDesc descriptor = convertProcessorArgument(StatDesc.class, scriptObject);
		String file = checkVariableSubstitution("file", checkPropertyValue("file", descriptor.getFile()));
		Stat stat = new Stat(getProcessorService(), displayName(descriptor), makeScheduledExecutor(descriptor), new java.io.File(file));
		return stat;
	}

}
